<template>
	<!-- 滑动采样轨迹图 -->
	<div class="track-chart-container">
		<div class="chart-title">滑动采样轨迹图 (X轴: 时间, Y轴: 水平位置)</div>
		<canvas ref="trackChart" class="track-chart"></canvas>
	</div>
	<div v-loading="isLoading" class="slider-container">
		<div
			class="slider-canvas"
			:style="{
				width: `${slideInfo.width}px`,
				height: `${slideInfo.height}px`,
			}"
			@click="() => {
				showAlert = false;
				updateSlideCaptcha()
			}"
		>
			<!-- 大图 -->
			<div
				ref="puzzle"
				class="puzzle-image"
				:style="{
					backgroundImage: `url(${slideInfo.puzzleUrl})`,
					width: '100%',
					height: '100%',
					position: 'absolute',
					top: 0,
					left: 0,
					backgroundSize: 'cover'
				}"
			/>
			<!-- 滑块 -->
			<div
				ref="block"
				class="slider-block"
				:style="{
					backgroundImage: `url(${slideInfo.sliderUrl})`,
					width: slideInfo.sliderSize + 'px',
					height: slideInfo.sliderSize + 'px',
					top: slideInfo.blockY + 'px',
					left: '0px',
					position: 'absolute',
					backgroundSize: 'cover'
				}"
			/>
			<!-- 结果遮罩层 -->
			<div
				:class="`result-mask ${resultMask.class}`"
				:style="{ height: `${resultMask.height}px` }"
			/>
		</div>
		<!-- 拖动的滑块内容 -->
		<div class="slider-square">
			<div class="box">
				<div
					class="slider-square-icon"
					:style="{ transform: `translateX(${slideInfo.sliderLeft}px)` }"
					@mousedown="sliderDown"
					@touchstart="sliderDown"
				></div>
				<span>{{ slideInfo.sliderText }}</span>
			</div>
		</div>
	</div>
	<div v-if="showAlert" class="custom-alert" @click="showAlert = false">
		<div class="alert-content" :style="{ color: alertColors }">
			<p>{{ alertMessage }}</p>
		</div>
	</div>
</template>

<script lang="ts" setup>
import {onDeactivated, onMounted, reactive, ref, watch} from 'vue'
import axios from 'axios'

const showAlert = ref(false)
const alertMessage = ref('')
const alertColors = ref('')
const trackChart = ref<HTMLCanvasElement | null>(null)

function showCustomAlert(message: string, color: string) {
	alertMessage.value = message
	alertColors.value = color
	showAlert.value = true
}

const isLoading = ref(false)
const props = withDefaults(defineProps<{
	loading: boolean;
}>(), {
	loading: false
})

watch(() => props.loading, (newVal) => {
	isLoading.value = newVal
})

// 定义 emits
const emit = defineEmits<{
	(
		e: 'validPass',
		value: {
			hash: string
			code: string
		}
	): void
}>()

defineExpose({
	updateSlideCaptcha
})

const puzzle = ref<HTMLDivElement | null>(null)
const block = ref<HTMLDivElement | null>(null)

// 通过此值可以限制展示的宽度
const showWidth = 300

const slideInfo = ref({
	hash: '',
	puzzleUrl: '',
	width: showWidth,
	height: showWidth * 3 / 4,
	sliderUrl: '',
	sliderSize: 0,
	sliderY: 0,
	sliderLeft: 0,
	blockX: 0,
	blockY: 0,
	scaleRatio: 1,
	sliderText: ''
})

const origin = reactive({
	originX: 0
})

const resultMask = reactive({
	class: '',
	height: 0
})

onMounted(() => {
	bindEvents()
	updateSlideCaptcha()
})

function verifySlideCaptcha(sliderX: number, hash: string, track: Array<{
	pointerX: number,
	pointerY: number,
	t: number
}>) {
	return axios.post<{
		result: boolean
		code: string
	}>('/api/validate', {
		sliderX,
		hash,
		track
	})
}

const slider = ref(false)

const track = ref<Array<{ pointerX: number, pointerY: number, t: number }>>([])

const sliderDown = (e: MouseEvent | TouchEvent) => {
	slider.value = true
	slideInfo.value.sliderText = ''
	const pageX = 'touches' in e ? e.touches[0].pageX : (e as MouseEvent).pageX
	origin.originX = pageX
	block.value && (block.value.style.willChange = 'transform')
	track.value = []
	const pageY = 'touches' in e ? e.touches[0].pageY : (e as MouseEvent).pageY
	track.value.push({pointerX: pageX, pointerY: pageY, t: Date.now()})
	e.preventDefault()
}

const sliderMove = (e: MouseEvent | TouchEvent) => {
	if (!slider.value) return
	const pageX = 'touches' in e ? e.touches[0].pageX : (e as MouseEvent).pageX
	const pageY = 'touches' in e ? e.touches[0].pageY : (e as MouseEvent).pageY
	const moveX = pageX - origin.originX
	const maxMoveX = (slideInfo.value.width - slideInfo.value.sliderSize)
	const clampedX = Math.max(0, Math.min(moveX, maxMoveX))
	block.value && (block.value.style.transform = `translate3d(${clampedX}px,0,0)`)
	slideInfo.value.sliderLeft = clampedX
	// 记录真实光标位置
	track.value.push({pointerX: pageX, pointerY: pageY, t: Date.now()})
	e.preventDefault()
}

const sliderUp = () => {
	if (!slider.value) return
	slider.value = false
	block.value && (block.value.style.willChange = 'auto')
	const resultX = slideInfo.value.sliderLeft / slideInfo.value.scaleRatio
	verifySlideCaptcha(resultX, slideInfo.value.hash, track.value)
		.then((res) => {
			if (res.data) {
				emit('validPass', {hash: slideInfo.value.hash, code: res.data.code})
				updateSlideCaptcha()
				showCustomAlert('验证成功', '#5ca862')
			} else {
				showCustomAlert('验证失败', '#ff0000')
				updateSlideCaptcha()
			}
			// 绘制轨迹图
			drawTrackChart()
		})
}

// 绘制轨迹图的函数
function drawTrackChart() {
	if (!trackChart.value || track.value.length === 0) return

	const canvas = trackChart.value
	const ctx = canvas.getContext('2d')
	if (!ctx) return

	// 设置画布尺寸
	canvas.width = canvas.clientWidth
	canvas.height = canvas.clientHeight

	// 清空画布
	ctx.clearRect(0, 0, canvas.width, canvas.height)

	// 获取轨迹数据
	const points = track.value
	if (points.length < 2) return

	// 计算时间范围和X坐标范围
	const minT = Math.min(...points.map(p => p.t))
	const maxT = Math.max(...points.map(p => p.t))
	const minX = Math.min(...points.map(p => p.pointerX))
	const maxX = Math.max(...points.map(p => p.pointerX))

	const rangeT = maxT - minT || 1
	const rangeX = maxX - minX || 1

	// 绘制轨迹线
	ctx.beginPath()
	ctx.strokeStyle = '#409eff'
	ctx.lineWidth = 2

	// 移动到第一个点 (时间作为X轴，X坐标作为Y轴)
	const firstPoint = points[0]
	const x1 = ((firstPoint.t - minT) / rangeT) * canvas.width
	const y1 = canvas.height - ((firstPoint.pointerX - minX) / rangeX) * canvas.height
	ctx.moveTo(x1, y1)

	// 绘制后续点
	for (let i = 1; i < points.length; i++) {
		const point = points[i]
		const x = ((point.t - minT) / rangeT) * canvas.width
		const y = canvas.height - ((point.pointerX - minX) / rangeX) * canvas.height
		ctx.lineTo(x, y)
	}

	ctx.stroke()

	// 绘制起始点和结束点
	ctx.beginPath()
	ctx.fillStyle = '#67c23a' // 起始点绿色
	ctx.arc(x1, y1, 5, 0, Math.PI * 2)
	ctx.fill()

	ctx.beginPath()
	ctx.fillStyle = '#f56c6c' // 结束点红色
	const lastPoint = points[points.length - 1]
	const x2 = ((lastPoint.t - minT) / rangeT) * canvas.width
	const y2 = canvas.height - ((lastPoint.pointerX - minX) / rangeX) * canvas.height
	ctx.arc(x2, y2, 5, 0, Math.PI * 2)
	ctx.fill()

	// 绘制坐标轴说明
	ctx.fillStyle = 'whitesmoke'
	ctx.font = '12px Arial'
	ctx.fillText('时间 (ms)', canvas.width / 2 - 30, canvas.height - 5)
	ctx.save()
	ctx.translate(10, canvas.height / 2)
	ctx.rotate(-Math.PI / 2)
	ctx.fillText('水平位置 (px)', 0, 0)
	ctx.restore()
}

// updateSlideCaptcha 中使用可选链防止 null 访问
function updateSlideCaptcha() {
	isLoading.value = true
	if (block.value) block.value.style.transform = 'translate3d(0,0,0)'
	slideInfo.value.sliderLeft = 0
	updateSlideInfo().then(() => {
		slideInfo.value.sliderText = '拖动滑块完成验证'
		resultMask.height = 0
		resultMask.class = ''
		slideInfo.value.sliderLeft = 0
		if (block.value) block.value.style.left = '0'
		if (puzzle.value) {
			slideInfo.value.scaleRatio = showWidth / slideInfo.value.width
			slideInfo.value.width *= slideInfo.value.scaleRatio
			slideInfo.value.height *= slideInfo.value.scaleRatio
			slideInfo.value.blockY *= slideInfo.value.scaleRatio
			slideInfo.value.sliderSize *= slideInfo.value.scaleRatio
		}
	}).finally(() => {
		isLoading.value = false
	})
}

function updateSlideInfo() {
	return axios.get<{
		hash: string
		puzzleUrl: string
		width: number
		height: number
		sliderUrl: string
		sliderSize: number
		sliderY: number
	}>('/api/slide?' + new Date().getTime())
		.then((res) => {
			slideInfo.value.hash = res.data.hash
			slideInfo.value.puzzleUrl = res.data.puzzleUrl
			slideInfo.value.width = res.data.width
			slideInfo.value.height = res.data.height
			slideInfo.value.sliderUrl = res.data.sliderUrl
			slideInfo.value.sliderSize = res.data.sliderSize
			slideInfo.value.blockY = res.data.sliderY
		})
}

const bindEvents = () => {
	document.addEventListener('mousemove', sliderMove)
	document.addEventListener('mouseup', sliderUp)
	document.addEventListener('touchmove', sliderMove, {passive: false})
	document.addEventListener('touchend', sliderUp)
}

onDeactivated(() => {
	document.removeEventListener('mousemove', sliderMove)
	document.removeEventListener('mouseup', sliderUp)
	document.removeEventListener('touchmove', sliderMove)
	document.removeEventListener('touchend', sliderUp)
})
</script>

<style lang="scss">
:root {
	--el-color-primary: #409eff;
}

.track-chart-container {
	margin-top: 16px;
	margin-bottom: 50px;
	width: 100%;
	height: 170px;
	.chart-title {
		text-align: center;
		font-size: 14px;
		color: whitesmoke;
		margin-bottom: 4px;
	}
	.track-chart {
		width: 100%;
		height: 100%;
		border: 1px solid whitesmoke;
		border-radius: 4px;
	}
}

.slider-container {
	position: relative;
	display: inline-block;
	flex-direction: column;
	justify-content: center;
	height: auto;
	margin: 0 auto;
	padding: 16px;
	border-radius: 12px;
	background: #fff;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

	.slider-canvas {
		position: relative;
		background-color: transparent;
		margin-bottom: 16px;

		.puzzle-image {
			border-radius: 4px;
		}
	}

	@keyframes result-show {
		0% {
			opacity: 0;
		}
		100% {
			opacity: 0.7;
		}
	}

	.result-mask {
		position: absolute;
		left: 0;
		bottom: 0;
		width: 100%;
		opacity: 0.7;

		&.success {
			background-color: #52ccba;
			animation: result-show 1s;
		}

		&.fail {
			background-color: #f57a7a;
			animation: result-show 1s;
		}
	}
}

.slider-square {
	background-color: #f7f9fa;
	height: 36px;
	text-align: center;
	line-height: 36px;
	border: 2px solid rgba(221, 221, 221, 0.17);
	position: relative;
	border-radius: 4px;
	z-index: 10;

	.box {
		padding: 0 16px;
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
		height: 100%;

		span {
			flex: 1;
			text-align: center;
			color: #666;
			font-size: 14px;
		}
	}

	.slider-square-icon {
		position: absolute;
		top: 0;
		left: 0;
		height: 35px;
		width: 35px;
		background-color: var(--el-color-primary);
		cursor: pointer;
		z-index: 11;
		user-select: none;
		will-change: transform;
		border: none;
		border-radius: 4px;

		&:hover,
		&:active {
			background-color: var(--el-color-primary);
			box-shadow: 0 0 5px var(--el-color-primary);
		}
	}
}
</style>