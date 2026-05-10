export interface GrowCardItem {
  icon: string;
  title: string;
  value?: number;
  total: number;
  color?: string;
  action?: string;
  footer?: string;
}

export const growCardList: GrowCardItem[] = [
  {
    title: '访问数',
    icon: 'visit-count|svg',
    value: 2000,
    total: 120000,
    color: 'green',
    action: '月',
  },
  {
    title: '成交额',
    icon: 'total-sales|svg',
    value: 20000,
    total: 500000,
    color: 'blue',
    action: '月',
  },
  {
    title: '下载数',
    icon: 'download-count|svg',
    value: 8000,
    total: 120000,
    color: 'orange',
    action: '周',
  },
  {
    title: '成交数',
    icon: 'transaction|svg',
    value: 5000,
    total: 50000,
    color: 'purple',
    action: '年',
  },
];

export const chartCardList: GrowCardItem[] = [
  {
    title: '总销售额',
    icon: 'visit-count|svg',
    total: 126560,
    value: 234.56,
    footer: '日均销售额',
  },
  {
    title: '订单量',
    icon: 'total-sales|svg',
    value: 1234,
    total: 8846,
    color: 'blue',
    footer: '日订单量',
  },
  {
    title: '支付笔数',
    icon: 'download-count|svg',
    value: 60,
    total: 6560,
    color: 'orange',
    footer: '转化率',
  },
  {
    title: '运营活动效果',
    icon: 'transaction|svg',
    total: 78,
  },
];
export const bdcCardList: GrowCardItem[] = [
  {
    title: '受理量',
    icon: 'ant-design:info-circle-outlined',
    total: 100,
    value: 60,
    footer: '今日受理量',
  },
  {
    title: '办结量',
    icon: 'ant-design:info-circle-outlined',
    value: 54,
    total: 87,
    color: 'blue',
    footer: '今日办结量',
  },
  {
    title: '用户受理量',
    icon: 'ant-design:info-circle-outlined',
    value: 13,
    total: 15,
    color: 'orange',
    footer: '用户今日受理量',
  },
  {
    title: '用户办结量',
    icon: 'ant-design:info-circle-outlined',
    total: 9,
    value: 7,
    footer: '用户今日办结量',
  },
];

export const table = {
  dataSource: [
    { reBizCode: '1', type: '转移登记', acceptBy: '张三', acceptDate: '2019-01-22', curNode: '任务分派', flowRate: 60 },
    { reBizCode: '2', type: '抵押登记', acceptBy: '李四', acceptDate: '2019-01-23', curNode: '领导审核', flowRate: 30 },
    { reBizCode: '3', type: '转移登记', acceptBy: '王武', acceptDate: '2019-01-25', curNode: '任务处理', flowRate: 20 },
    { reBizCode: '4', type: '转移登记', acceptBy: '赵楼', acceptDate: '2019-11-22', curNode: '部门审核', flowRate: 80 },
    { reBizCode: '5', type: '转移登记', acceptBy: '钱就', acceptDate: '2019-12-12', curNode: '任务分派', flowRate: 90 },
    { reBizCode: '6', type: '转移登记', acceptBy: '孙吧', acceptDate: '2019-03-06', curNode: '任务处理', flowRate: 10 },
    { reBizCode: '7', type: '抵押登记', acceptBy: '周大', acceptDate: '2019-04-13', curNode: '任务分派', flowRate: 100 },
    { reBizCode: '8', type: '抵押登记', acceptBy: '吴二', acceptDate: '2019-05-09', curNode: '任务上报', flowRate: 50 },
    { reBizCode: '9', type: '抵押登记', acceptBy: '郑爽', acceptDate: '2019-07-12', curNode: '任务处理', flowRate: 63 },
    { reBizCode: '20', type: '抵押登记', acceptBy: '林有', acceptDate: '2019-12-12', curNode: '任务打回', flowRate: 59 },
    { reBizCode: '11', type: '转移登记', acceptBy: '码云', acceptDate: '2019-09-10', curNode: '任务签收', flowRate: 87 },
  ],
  columns: [
    {
      title: '业务号',
      align: 'center',
      dataIndex: 'reBizCode',
    },
    {
      title: '业务类型',
      align: 'center',
      dataIndex: 'type',
    },
    {
      title: '受理人',
      align: 'center',
      dataIndex: 'acceptBy',
    },
    {
      title: '受理时间',
      align: 'center',
      dataIndex: 'acceptDate',
    },
    {
      title: '当前节点',
      align: 'center',
      dataIndex: 'curNode',
    },
    {
      title: '办理时长',
      align: 'center',
      dataIndex: 'flowRate',
    },
  ],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};
export const table1 = {
  dataSource: [
    { reBizCode: 'A001', type: '转移登记', acceptBy: '张四', acceptDate: '2019-01-22', curNode: '任务分派', flowRate: 12 },
    { reBizCode: 'A002', type: '抵押登记', acceptBy: '李吧', acceptDate: '2019-01-23', curNode: '任务签收', flowRate: 3 },
    { reBizCode: 'A003', type: '转移登记', acceptBy: '王三', acceptDate: '2019-01-25', curNode: '任务处理', flowRate: 24 },
    { reBizCode: 'A004', type: '转移登记', acceptBy: '赵二', acceptDate: '2019-11-22', curNode: '部门审核', flowRate: 10 },
    { reBizCode: 'A005', type: '转移登记', acceptBy: '钱大', acceptDate: '2019-12-12', curNode: '任务签收', flowRate: 8 },
    { reBizCode: 'A006', type: '转移登记', acceptBy: '孙就', acceptDate: '2019-03-06', curNode: '任务处理', flowRate: 10 },
    { reBizCode: 'A007', type: '抵押登记', acceptBy: '周晕', acceptDate: '2019-04-13', curNode: '部门审核', flowRate: 24 },
    { reBizCode: 'A008', type: '抵押登记', acceptBy: '吴有', acceptDate: '2019-05-09', curNode: '部门审核', flowRate: 30 },
    { reBizCode: 'A009', type: '抵押登记', acceptBy: '郑武', acceptDate: '2019-07-12', curNode: '任务分派', flowRate: 1 },
    { reBizCode: 'A0010', type: '抵押登记', acceptBy: '林爽', acceptDate: '2019-12-12', curNode: '部门审核', flowRate: 16 },
    { reBizCode: 'A0011', type: '转移登记', acceptBy: '码楼', acceptDate: '2019-09-10', curNode: '部门审核', flowRate: 7 },
  ],
  columns: [
    {
      title: '业务号',
      align: 'center',
      dataIndex: 'reBizCode',
    },
    {
      title: '受理人',
      align: 'center',
      dataIndex: 'acceptBy',
    },
    {
      title: '发起时间',
      align: 'center',
      dataIndex: 'acceptDate',
    },
    {
      title: '当前节点',
      align: 'center',
      dataIndex: 'curNode',
    },
    {
      title: '超时时间',
      align: 'center',
      dataIndex: 'flowRate',
    },
  ],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};

export interface DashboardCardItem {
  icon: string;
  title: string;
  value?: number;
  total?: number;
  color?: string;
  unit?: string;
  footer?: string;
  link?: string;
}

// 顶部4个核心指标卡
export const coreIndicatorList: DashboardCardItem[] = [
  {
    title: '今日入库',
    icon: 'ant-design:import-outlined',
    value: 0,
    total: 0,
    color: 'green',
    unit: '笔',
    footer: '本月累计入库',
   // link: '/mis/wms/MaterialInApprove,
  },
  {
    title: '今日出库',
    icon: 'ant-design:export-outlined',
    value: 0,
    total: 0,
    color: 'blue',
    unit: '笔',
    footer: '本月累计出库',
  //  link: '/mis/wms/ProductOutApprove',
  },
  {
    title: '在制批次',
    icon: 'ant-design:build-outlined',
    value: 0,
    total: 0,
    color: 'orange',
    unit: '批',
    footer: '今日新开工',
   // link: '/mis/mes/productionBatch',
  },
  {
    title: '待审单据',
    icon: 'ant-design:audit-outlined',
    value: 0,
    total: 0,
    color: 'red',
    unit: '单',
    footer: '待我审核',
   // link: '/dashboard/pending',
  },
];

// 待办类型映射
export const pendingTypeMap = {
  'STOCK_IN': { label: '入库审核', color: 'green', link: '/mis/wms/MaterialInApprove/MaterialInApproveList' },
  'STOCK_OUT': { label: '出库审核', color: 'blue', link: '/mis/wms/ProductOutApprove' },
  'PRODUCTION': { label: '生产工单', color: 'orange', link: '/mis/mes/productionTask' },
  'QUOTE': { label: '报价审核', color: 'purple', link: '/mis/scm/priceOfferApprove/PriceOfferApproveList' },
  'SALES_ORDER': { label: '销售订单', color: '#eb2f96', link: '/mis/scm/SalesOrderApply' },
  'DELIVERY': { label: '发货审核', color: '#13c2c2', link: '/mis/scm/delivery' },
};
