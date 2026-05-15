<template>
  <IndexDashboard v-if="indexStyle === 0" />
<!--  <IndexChart v-if="indexStyle === 1"></IndexChart>-->
<!--  <IndexDef v-if="indexStyle === 2"></IndexDef>-->
<!--  <IndexBdc v-if="indexStyle == 3"></IndexBdc>-->
<!--  <IndexTask v-if="indexStyle == 4"></IndexTask>-->
  <div style="width: 100%; text-align: right; margin-top: 20px">
    首页主题：
    <a-radio-group v-model:value="indexStyle">
      <a-radio :value="0">运营驾驶舱（新）</a-radio>
<!--      <a-radio :value="1">销量统计</a-radio>-->
<!--      <a-radio :value="2">默认</a-radio>-->
<!--      <a-radio :value="3">业务统计</a-radio>-->
<!--      <a-radio :value="4">我的任务</a-radio>-->
    </a-radio-group>
  </div>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import IndexDashboard from './homePage/IndexDashboard.vue';
  import IndexDef from './homePage/IndexDef.vue';
  import IndexChart from './homePage/IndexChart.vue';
  import IndexBdc from './homePage/IndexBdc.vue';
  import IndexTask from './homePage/IndexTask.vue';

  const indexStyle = ref(0);
</script>
