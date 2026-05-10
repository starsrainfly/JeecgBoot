<template>
  <div class="md:flex">
    <template v-for="(item, index) in dataList" :key="item.title">
      <Card
        size="small"
        :loading="loading"
        :title="item.title"
        class="md:w-1/4 w-full !md:mt-0 !mt-4 cursor-pointer hover:shadow-md transition-shadow"
        :class="[index + 1 < 4 && '!md:mr-4']"
        :canExpan="false"
        @click="handleClick(item)"
      >
        <template #extra>
          <Tag :color="item.color">{{ item.unit }}</Tag>
        </template>

        <div class="py-4 px-4 flex justify-between items-center">
          <CountTo :startVal="0" :endVal="item.value || 0" class="text-2xl font-bold" />
          <Icon :icon="item.icon" :size="40" :color="item.color" />
        </div>

        <div class="p-2 px-4 flex justify-between text-gray-500">
          <span>{{ item.footer }}</span>
          <CountTo :startVal="0" :endVal="item.total || 0" />
        </div>
      </Card>
    </template>
  </div>
</template>

<script lang="ts" setup>
  import { CountTo } from '/@/components/CountTo/index';
  import { Icon } from '/@/components/Icon';
  import { Tag, Card } from 'ant-design-vue';
  import { useRouter } from 'vue-router';

  defineProps({
    loading: { type: Boolean },
    dataList: { type: Array, default: () => [] },
  });

  const router = useRouter();

  function handleClick(item: any) {
    if (item.link) {
      router.push(item.link);
    }
  }
</script>
