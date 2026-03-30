<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="title"
    :width="1024"
    @ok="handleSubmit"
    :showCancelBtn="showCancelBtn"
    :showOkBtn="showOkBtn"
  >
    <!-- 核心：使用 BasicForm 渲染传入的 Schema -->
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { FormSchema } from '/@/components/Form';
  import {saveOrUpdate} from "@/views/mis/mes/productionTask/ProductionTask.api";
  import {
    completeTask,
    setBatchStatus,
    startTask,
    startWeighing
  } from "@/views/mis/mes/myTask/MyTask.api";

  // 定义 Props 接口
  // 这里的 Props 用于接收父组件传来的控制参数
  interface Props {
    // 表单配置 (关键：接收外部传入的 Schema)
    schemas?: FormSchema[];
    // 是否显示底部按钮 (false 代表详情只读模式)
    showFooter?: boolean;
    // 标题
    title?: string;
    mode?:string;
  }

  // 设置默认值
  const props = withDefaults(defineProps<Props>(), {
    // 默认为空数组，防止报错
    schemas: () => [],
    showFooter: true,
    title: '生产开工',
    mode:'START',//默认是开工
  });

  // 定义 Emits
  const emit = defineEmits(['register', 'success']);

  // 1. Modal 控制
  // useModalInner 是 JeecgBoot 风格的弹窗通信钩子
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    // data 包含 record(数据) 和 其他控制参数
// 检查传入的 data 中是否有 record
    if (data?.record) {
      // 调用表单的 setFieldsValue 方法填充数据
      // 这里的 data.record 应该是一个对象，例如 { taskNo: 'WO001', productName: '产品A' }
      setFieldsValue(data.record);
    }
    // 动态设置 Modal 的底部按钮
    // 如果传入的 data.showFooter 为 false，说明是详情页，隐藏确定按钮

    setModalProps({ confirmLoading: false });
    // --- 关键修复点 1：动态设置表单 Schema ---
    // 获取外部传入的 schemas (来自 data 或 props)
    const schemas = data?.schemas || props.schemas;
    if (schemas && schemas.length > 0) {
      // 使用 setProps 动态更新表单的 schemas
      setFieldsValue({ schemas }); // 注意：这里可能需要根据你的 Form 版本调整
      // 更通用的做法是调用 form 的方法，但 JEECG 的 useForm 通常通过 register 时的配置决定
    }

    // --- 关键修复点 2：处理只读逻辑 ---
    // 这里需要根据 data.showFooter 来决定是否禁用表单
    // 注意：JEECG 的 Form disabled 逻辑
    const isDetailMode = !(data?.showFooter ?? props.showFooter);

    // 3. 更新表单属性 (包含动态传入的 schemas 和 disabled 状态)
    // 这里的逻辑是 useForm 的核心重置逻辑
    try {
      // 重置表单值，防止残留
      resetFields();

      // 设置新的 Schema 和禁用状态
      setProps({
        schemas: schemas, // 核心：把传进来的 Schema 应用到表单上
        disabled: isDetailMode
      });

      // 4. 赋值数据
      if (data?.record) {
        setFieldsValue({
          ...data.record,
        });
      }
    } catch (error) {
      console.error('表单初始化失败', error);
    }
    // // 2. Form 控制
    // // 先重置表单，防止残留旧数据
    // await resetFields();
    //
    // // 关键逻辑：根据 showFooter 动态禁用表单
    // // 如果是详情页 (showFooter=false)，则禁用所有表单项
    // // 如果是编辑页 (showFooter=true)，则启用表单
    // const isDetailMode = !(data?.showFooter ?? props.showFooter);
    // setProps({ disabled: isDetailMode });
    //
    // // 3. 数据赋值
    // // 如果有 record，就赋值给表单
    // if (data?.record) {
    //   await setFieldsValue({
    //     ...data.record,
    //   });
    // }
  });

  // 4. Form 注册与控制
  // 这里接收外部传入的 props.schemas
  // 注意：这里没有写死 Schema，而是动态接收
  const [registerForm, {
    setProps,
    resetFields,
    setFieldsValue,
    validate
  }] = useForm({
    // 使用传入的 schemas
    schemas: props.schemas,
    // 统一的布局配置 (你可以根据需要调整)
    labelWidth: 120,
    // 默认显示操作按钮组，但会被上面的 setProps 覆盖
    showActionButtonGroup: false,
    // 默认栅格布局
    baseColProps: { span: 8 },
  });

  // 计算标题
  const title = computed(() => props.title);

  // 提交事件
  // 这是一个通用的提交函数
  async function handleSubmit() {
    try {
      // 1. 表单验证
      let values = await validate();

      // 2. 开启 Loading
      setModalProps({ confirmLoading: true });

      // --- 核心业务逻辑区 ---
      // 这里需要你根据具体的业务 API 修改
      // 例如：调用 saveOrUpdate 或者 专门的 completeTask API
      // await saveOrUpdate(values);
      // -------------------
      // await completeTask(values);
      await startTask(values);
      if(values.taskType === 'weighing'){
        await startWeighing({id:values.batchId});
      }

      //if(props.mode == 'START'){
      //  await startTask(values);
     // }
      // if(props.mode === 'COMPLETE'){
      //   await completeTask(values);
      // }
      // 3. 关闭弹窗
      closeModal();

      // 4. 刷新列表
      emit('success');

    } catch (error) {
      // 验证失败处理
      console.error('表单验证失败', error);
      return Promise.reject(error);
    } finally {
      // 关闭 Loading
      setModalProps({ confirmLoading: false });
    }
  }
</script>
