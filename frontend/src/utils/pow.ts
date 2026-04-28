import {loadCryptoJs} from './cryptoJsLoader'

function hasLeadingZeroNibblesInWords(words: number[], zeroNibbles: number) {
	for (let nibbleIndex = 0; nibbleIndex < zeroNibbles; nibbleIndex++) {
		const wordIndex = Math.floor(nibbleIndex / 8)
		const nibbleOffset = nibbleIndex % 8
		const shift = 28 - nibbleOffset * 4
		if ((((words[wordIndex] ?? 0) >>> shift) & 0x0f) !== 0) return false
	}
	return true
}

export async function solvePow(hash: string, powSalt: string, difficulty: number): Promise<string> {
	const normalizedDifficulty = Math.max(0, difficulty)
	const inputPrefix = `${hash}:${powSalt}:`
	if (normalizedDifficulty === 0) return '0'

	const cryptoJs = await loadCryptoJs()
	if (cryptoJs?.SHA256) {
		for (let nonce = 0; nonce < 5_000_000; nonce++) {
			const nonceText = nonce.toString(16)
			if (hasLeadingZeroNibblesInWords(cryptoJs.SHA256(inputPrefix + nonceText).words, normalizedDifficulty)) {
				return nonceText
			}
		}
		throw new Error('PoW solve failed')
	}

	throw new Error('SHA-256 unavailable: CryptoJS is unavailable')
}

