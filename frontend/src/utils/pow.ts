import CryptoJS from 'crypto-js'

const MAX_NONCE = 5_000_000

function hasLeadingZeroNibblesInWords(words: number[], zeroNibbles: number) {
	for (let nibbleIndex = 0; nibbleIndex < zeroNibbles; nibbleIndex++) {
		const wordIndex = Math.floor(nibbleIndex / 8)
		const nibbleOffset = nibbleIndex % 8
		const shift = 28 - nibbleOffset * 4
		if ((((words[wordIndex] ?? 0) >>> shift) & 0x0f) !== 0) return false
	}
	return true
}

function yieldToMainThread() {
	return new Promise((resolve) => {
		setTimeout(resolve, 0)
	})
}

export async function solvePow(
	hash: string,
	powSalt: string,
	difficulty: number
): Promise<string | null> {
	const normalizedDifficulty = Math.max(0, difficulty)
	const inputPrefix = `${hash}:${powSalt}:`
	if (normalizedDifficulty === 0) return '0'

	await yieldToMainThread()

	// 记录上一次让出主线程的时间
	let lastYieldTime = performance.now()

	for (let nonce = 0; nonce < MAX_NONCE; nonce++) {
		const nonceText = nonce.toString(16)
		if (
			hasLeadingZeroNibblesInWords(
				CryptoJS.SHA256(inputPrefix + nonceText).words,
				normalizedDifficulty
			)
		) {
			return nonceText
		}

		// 每计算 1000 次，检查一下时间（不要每次都检查，performance.now 也有极小的开销）
		if (nonce > 0 && nonce % 1000 === 0) {
			const currentTime = performance.now()
			// 如果距离上次让出线程已经超过了 16 毫秒（约 1 帧的时间）
			if (currentTime - lastYieldTime > 16) {
				await yieldToMainThread()
				// 醒来后更新时间
				lastYieldTime = performance.now()
			}
		}
	}
	return null
}
