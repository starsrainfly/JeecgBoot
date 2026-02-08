<!-- src/views/mis/scm/salesOrder/components/PriceSelectModal.vue -->
<template>
  <BasicModal @register="registerModal" title="价格选择测试" width="600px">
    <p>价格策略弹窗（已加载）</p>
    <a-button type="primary" @click="handleSelect">选择测试项</a-button>
  </BasicModal>
</template>

<script setup lang="ts">
  import { BasicModal, useModalInner } from '/@/components/Modal'

  const emit = defineEmits(['select'])

  const [registerModal] = useModalInner(() => {
    console.log('PriceSelectModal opened')
  })

  function handleSelect() {
    emit('select', { itemCode: 'TEST001', itemName: '测试产品', price: 99.9 })
  }
</script>
