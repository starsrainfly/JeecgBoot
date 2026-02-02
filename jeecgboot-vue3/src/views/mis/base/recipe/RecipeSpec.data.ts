import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '颜色',
    align:"center",
    dataIndex: 'color'
   },
   {
    title: '触变因子',
    align:"center",
    dataIndex: 'thixotropyFactor'
   },
   {
    title: '比重',
    align:"center",
    dataIndex: 'specificWeight'
   },
   {
    title: '粘度（mPa·s）',
    align:"center",
    dataIndex: 'viscosity'
   },
   {
    title: 'A组分比重',
    align:"center",
    dataIndex: 'specificWeightA'
   },
   {
    title: 'B组分比重',
    align:"center",
    dataIndex: 'specificWeightB'
   },
   {
    title: 'A组分粘度',
    align:"center",
    dataIndex: 'viscosityA'
   },
   {
    title: 'B组分粘度',
    align:"center",
    dataIndex: 'viscosityB'
   },
   {
    title: '混合后粘度',
    align:"center",
    dataIndex: 'viscosityMix'
   },
   {
    title: '硬度（Shore D）',
    align:"center",
    dataIndex: 'hardness'
   },
   {
    title: '拉伸强度（MPa）',
    align:"center",
    dataIndex: 'pull'
   },
   {
    title: '抗弯强度（MPa）',
    align:"center",
    dataIndex: 'bending'
   },
   {
    title: '抗压强度（MPa）',
    align:"center",
    dataIndex: 'compression'
   },
   {
    title: '剪切强度（MPa）',
    align:"center",
    dataIndex: 'shearBondStrength'
   },
   {
    title: '体积电阻率（Ω·cm）',
    align:"center",
    dataIndex: 'volumeResistivity'
   },
   {
    title: '电气强度（kV/mm）',
    align:"center",
    dataIndex: 'electricStrength'
   },
   {
    title: '击穿电压（kV）',
    align:"center",
    dataIndex: 'breakdownVoltage'
   },
   {
    title: '介电常数（@1kHz）',
    align:"center",
    dataIndex: 'dielectricConstant'
   },
   {
    title: '膨胀系数（1/℃）',
    align:"center",
    dataIndex: 'expansivity'
   },
   {
    title: '吸水率（%）',
    align:"center",
    dataIndex: 'waterAbsorption'
   },
   {
    title: '耐温范围（如 -50~200℃）',
    align:"center",
    dataIndex: 'temperature'
   },
   {
    title: '固化条件（如 25℃/24h）',
    align:"center",
    dataIndex: 'cureCondition'
   },
   {
    title: '光泽度',
    align:"center",
    dataIndex: 'gloss'
   },
   {
    title: '胶化时间（分）',
    align:"center",
    dataIndex: 'gelTime'
   },
   {
    title: '流动性（如 无流淌）',
    align:"center",
    dataIndex: 'mobility'
   },
   {
    title: 'A:B 配比（如 100:30）',
    align:"center",
    dataIndex: 'proportionAb'
   },
  {
    field: 'recipeId',
    label: '配方ID',
    component: 'Input', // 或者用 'Hidden' 如果有该组件
    show: false        // 👈 不显示在表单中

  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '颜色',
    field: 'color',
    component: 'Input',
  },
  {
    label: '触变因子',
    field: 'thixotropyFactor',
    component: 'InputNumber',
  },
  {
    label: '比重',
    field: 'specificWeight',
    component: 'InputNumber',
  },
  {
    label: '粘度（mPa·s）',
    field: 'viscosity',
    component: 'InputNumber',
  },
  {
    label: 'A组分比重',
    field: 'specificWeightA',
    component: 'InputNumber',
  },
  {
    label: 'B组分比重',
    field: 'specificWeightB',
    component: 'InputNumber',
  },
  {
    label: 'A组分粘度',
    field: 'viscosityA',
    component: 'InputNumber',
  },
  {
    label: 'B组分粘度',
    field: 'viscosityB',
    component: 'InputNumber',
  },
  {
    label: '混合后粘度',
    field: 'viscosityMix',
    component: 'InputNumber',
  },
  {
    label: '硬度（Shore D）',
    field: 'hardness',
    component: 'InputNumber',
  },
  {
    label: '拉伸强度（MPa）',
    field: 'pull',
    component: 'InputNumber',
  },
  {
    label: '抗弯强度（MPa）',
    field: 'bending',
    component: 'InputNumber',
  },
  {
    label: '抗压强度（MPa）',
    field: 'compression',
    component: 'InputNumber',
  },
  {
    label: '剪切强度（MPa）',
    field: 'shearBondStrength',
    component: 'InputNumber',
  },
  {
    label: '体积电阻率（Ω·cm）',
    field: 'volumeResistivity',
    component: 'InputNumber',
  },
  {
    label: '电气强度（kV/mm）',
    field: 'electricStrength',
    component: 'InputNumber',
  },
  {
    label: '击穿电压（kV）',
    field: 'breakdownVoltage',
    component: 'InputNumber',
  },
  {
    label: '介电常数（@1kHz）',
    field: 'dielectricConstant',
    component: 'InputNumber',
  },
  {
    label: '膨胀系数（1/℃）',
    field: 'expansivity',
    component: 'InputNumber',
  },
  {
    label: '吸水率（%）',
    field: 'waterAbsorption',
    component: 'InputNumber',
  },
  {
    label: '耐温范围',
    field: 'temperature',
    component: 'Input',
  },
  {
    label: '固化条件（如 25℃/24h）',
    field: 'cureCondition',
    component: 'Input',
  },
  {
    label: '光泽度',
    field: 'gloss',
    component: 'InputNumber',
  },
  {
    label: '胶化时间（分）',
    field: 'gelTime',
    component: 'InputNumber',
  },
  {
    label: '流动性（如 无流淌）',
    field: 'mobility',
    component: 'Input',
  },
  {
    label: 'A:B 配比（如 100:30）',
    field: 'proportionAb',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
  {
    field: 'recipeId',
    label: '配方ID',
    component: 'Input', // 或者用 'Hidden' 如果有该组件
    show: false        // 👈 不显示在表单中

  },
];

// 高级查询数据
export const superQuerySchema = {
  color: {title: '颜色',order: 0,view: 'text', type: 'string',},
  thixotropyFactor: {title: '触变因子',order: 1,view: 'number', type: 'number',},
  specificWeight: {title: '比重',order: 2,view: 'number', type: 'number',},
  viscosity: {title: '粘度（mPa·s）',order: 3,view: 'number', type: 'number',},
  specificWeightA: {title: 'A组分比重',order: 4,view: 'number', type: 'number',},
  specificWeightB: {title: 'B组分比重',order: 5,view: 'number', type: 'number',},
  viscosityA: {title: 'A组分粘度',order: 6,view: 'number', type: 'number',},
  viscosityB: {title: 'B组分粘度',order: 7,view: 'number', type: 'number',},
  viscosityMix: {title: '混合后粘度',order: 8,view: 'number', type: 'number',},
  hardness: {title: '硬度（Shore D）',order: 9,view: 'number', type: 'number',},
  pull: {title: '拉伸强度（MPa）',order: 10,view: 'number', type: 'number',},
  bending: {title: '抗弯强度（MPa）',order: 11,view: 'number', type: 'number',},
  compression: {title: '抗压强度（MPa）',order: 12,view: 'number', type: 'number',},
  shearBondStrength: {title: '剪切强度（MPa）',order: 13,view: 'number', type: 'number',},
  volumeResistivity: {title: '体积电阻率（Ω·cm）',order: 14,view: 'number', type: 'number',},
  electricStrength: {title: '电气强度（kV/mm）',order: 15,view: 'number', type: 'number',},
  breakdownVoltage: {title: '击穿电压（kV）',order: 16,view: 'number', type: 'number',},
  dielectricConstant: {title: '介电常数（@1kHz）',order: 17,view: 'number', type: 'number',},
  expansivity: {title: '膨胀系数（1/℃）',order: 18,view: 'number', type: 'number',},
  waterAbsorption: {title: '吸水率（%）',order: 19,view: 'number', type: 'number',},
  temperature: {title: '耐温范围（如 -50~200℃）',order: 20,view: 'text', type: 'string',},
  cureCondition: {title: '固化条件（如 25℃/24h）',order: 21,view: 'text', type: 'string',},
  gloss: {title: '光泽度',order: 22,view: 'number', type: 'number',},
  gelTime: {title: '胶化时间（分）',order: 23,view: 'number', type: 'number',},
  mobility: {title: '流动性（如 无流淌）',order: 24,view: 'text', type: 'string',},
  proportionAb: {title: 'A:B 配比（如 100:30）',order: 25,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
