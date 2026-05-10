<template>
  <Card title="快捷导航" v-bind="$attrs">
    <template v-for="item in navItems" :key="item">
      <CardGrid @click="goPage">
        <span class="flex flex-col items-center">
          <Icon :icon="item.icon" :color="item.color" size="20" />
          <span class="text-md mt-2">{{ item.title }}</span>
        </span>
      </CardGrid>
    </template>
  </Card>
</template>
<script lang="ts" setup>
  import { Card } from 'ant-design-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';
  import {router} from "@/router";

  const CardGrid = Card.Grid;
  const $message = useMessage();
  const navItems = [
    // {
    //   title: '业务受理',
    //   icon: 'ion:home-outline',
    //   color: '#1fdaca',
    // },
    // {
    //   title: '业务管理',
    //   icon: 'ion:grid-outline',
    //   color: '#bf0c2c',
    // },
    // {
    //   title: '文件管理',
    //   icon: 'ion:layers-outline',
    //   color: '#e18525',
    // },
    // {
    //   title: '信息查询',
    //   icon: 'ion:settings-outline',
    //   color: '#3fb27f',
    // },
		// {
		// 		title: '通知公告',
		// 		icon: 'ion:notifications',
		// 		color: '#13b0ff',
		// },
		// {
		// 		title: '我的任务',
		// 		icon: 'ion:person-stalker',
		// 		color: '#b27315',
		// }
    { title: '入库申请', icon: 'ant-design:import-outlined', color: '#52c41a', link: '/mis/wms/MaterialIn/StockInList' },
    { title: '出库申请', icon: 'ant-design:export-outlined', color: '#1890ff', link: '/mis/wms/ProductOut/ProductOutList' },
    { title: '生产工单', icon: 'ant-design:build-outlined', color: '#faad14', link: '/mis/mes/productionOrder' },
    { title: '销售订单', icon: 'ant-design:shopping-outlined', color: '#eb2f96', link: '/mis/scm/SalesOrderApply' },
    { title: '报价管理', icon: 'ant-design:dollar-outlined', color: '#722ed1', link: '/mis/scm/priceOffer/PriceOfferList' },
    { title: '回款管理', icon: 'ant-design:credit-card-outlined', color: '#52c41a', link: '/mis/scm/receivable/receipt' },
    { title: '库存查询', icon: 'ant-design:database-outlined', color: '#13c2c2', link: '/mis/wms/stock/StockList' },
    { title: '标签打印', icon: 'ant-design:printer-outlined', color: '#f5222d', link: '/mis/mes/labelPrintTask' },
  ];

  // function goPage() {
  //   $message.createMessage.success('根据业务自行处理跳转页面!');
  // }
  function goPage(item: any) {
    if (item.link) {
      router.push(item.link);
    }

  }
</script>
