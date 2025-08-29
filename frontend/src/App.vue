<template>
	<div class="container">
		<SlipCaptcha
			ref="slideCaptchaRef"
			:loading="loading"
			@valid-pass="slideCaptchaSuccess"
		/>
	</div>
</template>
<script setup lang="ts">
import SlipCaptcha from "./components/SlipCaptcha.vue";
import {ref} from "vue";

const loading = ref<boolean>(false)
const slideCaptchaSuccess = () => {
	// Do  something
}
</script>
<style scoped>
.container {
	background: color-mix(in srgb, #fff, transparent 90%);
	backdrop-filter: blur(10px);
	border-radius: 16px;
	border: 1px solid color-mix(in srgb, #dcdee0, transparent 50%);
	min-width: auto;
	width: auto;
	padding: 25px;
}
</style>
