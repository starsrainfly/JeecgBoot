<template>
  <a-card :bordered="false" :body-style="{ padding: '20px' }" hoverable @click="handleClick">
    <div class="stat-card">
      <div class="icon-wrapper" :style="{ backgroundColor: color + '15' }">
        <component :is="icon" :style="{ color: color, fontSize: '24px' }" />
      </div>
      <div class="content">
        <div class="title">{{ title }}</div>
        <div class="value" :style="{ color: color }">
          {{ displayValue }}
          <span v-if="suffix" class="suffix">{{ suffix }}</span>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
  import { computed } from 'vue';
  import { useRouter } from 'vue-router';

  const props = defineProps<{
    title: string;
    value: number | string;
    color: string;
    icon: string;
    suffix?: string;
    prefix?: string;   // ← 只加这一行，显式声明
    link?: any;
  }>();

  const router = useRouter();

  const displayValue = computed(() => {
    const val = props.value;

    // 处理 undefined/null
    if (val === undefined || val === null) {
      return '0';
    }

    const num = Number(val);

    // 处理 NaN
    if (isNaN(num)) {
      return String(val);
    }

    // 大于1万显示 x.x万
    if (num >= 10000) {
      return (num / 10000).toFixed(1) + '万';
    }

    // 正常数字
    return num.toLocaleString();
  });

  function handleClick() {
    if (props.link) {
      router.push(props.link);
    }
  }
</script>

<style scoped lang="less">
  .stat-card {
    display: flex;
    align-items: center;
    gap: 16px;

    .icon-wrapper {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .content {
      flex: 1;

      .title {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
        margin-bottom: 4px;
      }

      .value {
        font-size: 28px;
        font-weight: 600;
        line-height: 1;

        .suffix {
          font-size: 14px;
          font-weight: normal;
          margin-left: 4px;
          color: rgba(0, 0, 0, 0.65);
        }
      }
    }
  }
</style>
