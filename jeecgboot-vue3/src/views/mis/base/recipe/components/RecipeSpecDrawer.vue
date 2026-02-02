<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    :title="getTitle"
    :width="adaptiveWidth"
    @ok="handleSubmit"
    :showFooter="showFooter"
    destroyOnClose
  >
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>

<script lang="ts" setup>
  import {defineComponent, ref, computed, unref, useAttrs} from 'vue';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import {formSchema} from '../RecipeSpec.data';
  import {saveOrUpdate} from '../RecipeSpec.api';
  import { useDrawerAdaptiveWidth } from '/@/hooks/jeecg/useAdaptiveWidth';

  // Emits声明
  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);

  const rowId = ref('');
  const { adaptiveWidth } = useDrawerAdaptiveWidth();
  const currentRecipeId = ref('');
  //表单配置
  const [registerForm, { setProps,resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 12},
    labelCol: { span: 12 },
    wrapperCol: { span: 12 },
    //labelWidth:90,
    labelAlign:"left"
  });
  const showFooter = ref(true);
  //表单赋值
  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    //重置表单
    await resetFields();
    const isUpdateVal = !!data?.isUpdate; // 👈 先定义局部变量
    showFooter.value = data?.showFooter ?? true;
    setDrawerProps({ confirmLoading: false, showFooter: showFooter.value });
    isUpdate.value = !!data?.isUpdate;

    if(data?.record?.recipeId){
      currentRecipeId.value = data.record.recipeId;
    }
    // if (unref(isUpdate)) {
    //   //表单赋值
    //   await setFieldsValue({
    //     ...data.record,
    //   });
    // }
    // 直接使用传入的 record（新增时为空对象，编辑时为子表数据）
    await setFieldsValue({
      ...data.record,
      // 确保新增时有 recipeId（虽然理论上已保证）
      ...(isUpdateVal ? {} : { recipeId: data.recipeId }),
    });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter })
  });
  //获取标题
  const getTitle = computed(() => {
    // update-begin--author:liaozhiyang---date:20240306---for：【QQYUN-8389】系统用户详情抽屉title更改
    if (!unref(isUpdate)) {
      return '新增属性';
    } else {
      return unref(showFooter) ? '编辑属性' : '属性详情';
    }
    // update-end--author:liaozhiyang---date:20240306---for：【QQYUN-8389】系统用户详情抽屉title更改
  });

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();

      values = {
        ...values,
        recipeId: unref(currentRecipeId)
      };
      // 确保新增时 recipeId 存在
      if (!isUpdate.value && !values.recipeId) {
        // 可以从外部传入的 data 中再取一次，或报错
        return;
      }

      setDrawerProps({ confirmLoading: true });
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      //关闭弹窗
      closeDrawer();
      //刷新列表
      emit('success');
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    } finally {
     // setModalProps({confirmLoading: false});
      setDrawerProps({ confirmLoading: false });
    }
  }


</script>

<style scoped>

</style>
