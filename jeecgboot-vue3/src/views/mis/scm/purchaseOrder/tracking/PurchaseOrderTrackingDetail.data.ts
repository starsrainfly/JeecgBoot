import { BasicColumn } from '@/components/Table';
import { h } from 'vue';  // 新增

// ==================== 详情弹窗子表列 ====================
export const detailColumns: BasicColumn[] = [
  {
    title: '物料编码',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '物料名称',
    dataIndex: 'goodsName',
    width: 180,
  },
  {
    title: '规格型号',
    dataIndex: 'goodsSpec',
    width: 120,
  },
  {
    title: '单位',
    dataIndex: 'unit',
    width: 80,

  },
  {
    title: '采购数量',
    dataIndex: 'orderQty',
    width: 100,
    align: 'right',
  },
  {
    title: '已入库数量',
    dataIndex: 'receivedQty',
    width: 100,
    align: 'right',
  },
  {
    title: '在途申请数量',
    dataIndex: 'appliedQty',
    width: 110,
    align: 'right',
  },
  {
    title: '剩余可到货',
    dataIndex: 'remainingQty',
    width: 110,
    align: 'right',
    customRender: ({ text }) => {
      if (text === null || text === undefined) return '-';
      const val = Number(text);
      // if (val < 0) return <span style="color: #ff4d4f">{text}</span>;
      if (val < 0) {
        // 原来：return <span style="color: #ff4d4f">{text}</span>;
        return h('span', { style: { color: '#ff4d4f' } }, text);
      }
      return text;
    },
  },
  {
    title: '含税单价',
    dataIndex: 'unitPrice',
    width: 100,
    align: 'right',
  },
  {
    title: '含税金额',
    dataIndex: 'detailAmount',
    width: 100,
    align: 'right',
  },
  {
    title: '期望到货日期',
    dataIndex: 'expectedDate',
    width: 120,
  },
  {
    title: '明细状态',
    dataIndex: 'detailStatus_dictText',
    width: 100,

  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
    ellipsis: true,
  },
];
