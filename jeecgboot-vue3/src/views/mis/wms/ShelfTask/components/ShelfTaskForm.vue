<!-- src/views/wms/shelfTask/components/ShelfTaskForm.vue -->
<template>
  <div style="min-height: 400px">
    <BasicForm @register="registerForm"></BasicForm>
    <div style="width: 100%; text-align: center" v-if="!formDisabled">
      <a-button @click="submitForm" pre-icon="ant-design:check" type="primary">提 交</a-button>
    </div>
  </div>
</template>

<script lang="ts">
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { computed, defineComponent } from 'vue';
  import { propTypes } from '/@/utils/propTypes';
  import { shelfFormSchema } from '../ShelfTask.data';
  import { doShelf, batchShelf } from '../ShelfTask.api';

  export default defineComponent({
    name: 'ShelfTaskForm',
    components: {
      BasicForm,
    },
    props: {
      formData: propTypes.object.def({}),
      isBatch: propTypes.bool.def(false),
    },
    setup(props) {
      const [registerForm, { setFieldsValue, setProps, getFieldsValue }] = useForm({
        labelWidth: 120,
        schemas: shelfFormSchema,
        showActionButtonGroup: false,
        baseColProps: { span: 12 },
      });

      const formDisabled = computed(() => {
        if (props.formData.disabled === false) {
          return false;
        }
        return true;
      });

      let formData = {};

      async function initFormData() {
        // 单条上架时回显数据
        if (!props.isBatch && props.formData.record) {
          const record = props.formData.record;
          formData = {
            stockId: record.id,
            goodsName: record.goodsName,
            goodsCode: record.goodsCode,
            goodsSpec: record.goodsSpec,
            batchNo: record.batchNo,
            quantity: record.quantity,
            unit: record.unit,
            shelfQty: record.quantity,
          };
          await setFieldsValue(formData);
        }
        await setProps({ disabled: formDisabled.value });
      }

      async function submitForm() {
        const data = getFieldsValue();
        const params = Object.assign({}, formData, data);
        console.log('上架表单数据', params);

        if (props.isBatch && props.formData.records) {
          // 批量上架
          const list = props.formData.records.map((record) => ({
            stockId: record.id,
            toWarehouseId: params.toWarehouseId,
            toAreaId: params.toAreaId,
            toShelfId: params.toShelfId,
            toLocationId: params.toLocationId,
            shelfQty: record.quantity,
            remark: params.remark,
          }));
          await batchShelf(list);
        } else {
          // 单条上架
          await doShelf({
            stockId: params.stockId,
            toWarehouseId: params.toWarehouseId,
            toAreaId: params.toAreaId,
            toShelfId: params.toShelfId,
            toLocationId: params.toLocationId,
            shelfQty: params.shelfQty,
            remark: params.remark,
          });
        }
      }

      initFormData();

      return {
        registerForm,
        formDisabled,
        submitForm,
      };
    },
  });
</script>
