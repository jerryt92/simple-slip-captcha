export type CryptoJSLike = {
	SHA256: (input: string) => {
		words: number[]
		toString: () => string
	}
}

type GlobalWithCryptoJS = typeof globalThis & {
	CryptoJS?: CryptoJSLike
}

let cryptoJsPromise: Promise<CryptoJSLike> | null = null

function getGlobalCryptoJs() {
	return (globalThis as GlobalWithCryptoJS).CryptoJS
}

export function loadCryptoJs(): Promise<CryptoJSLike> {
	const existing = getGlobalCryptoJs()
	if (existing) return Promise.resolve(existing)
	if (cryptoJsPromise) return cryptoJsPromise

	cryptoJsPromise = new Promise((resolve, reject) => {
		if (typeof document === 'undefined') {
			reject(new Error('CryptoJS loader requires a browser environment'))
			return
		}

		const existingScript = document.querySelector<HTMLScriptElement>('script[data-cryptojs-loader="true"]')
		if (existingScript) {
			existingScript.addEventListener('load', () => {
				const loaded = getGlobalCryptoJs()
				if (loaded) resolve(loaded)
			}, {once: true})
			existingScript.addEventListener('error', () => {
				cryptoJsPromise = null
				reject(new Error('Failed to load CryptoJS script'))
			}, {once: true})
			return
		}

		const script = document.createElement('script')
		script.src = '/lib/crypto-js.min.js'
		script.async = true
		script.dataset.cryptojsLoader = 'true'

		script.onload = () => {
			const loaded = getGlobalCryptoJs()
			if (loaded) {
				resolve(loaded)
				return
			}
			cryptoJsPromise = null
			reject(new Error('CryptoJS loaded but global export is missing'))
		}

		script.onerror = () => {
			cryptoJsPromise = null
			reject(new Error('Failed to load CryptoJS script'))
		}

		document.head.appendChild(script)
	})

	return cryptoJsPromise
}

