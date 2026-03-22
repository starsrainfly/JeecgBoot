<script lang="ts">
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { computed, defineComponent, reactive, ref, unref, watch, nextTick } from 'vue';
  import {defHttp} from '/@/utils/http/axios';
  import { propTypes } from '/@/utils/propTypes';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  import {getBpmFormSchema,recipeDetailColumns} from '../Recipe.data';
  import {saveOrUpdate,recipeDetailList} from '../Recipe.api';

  export default defineComponent({
    name: "RecipeForm",
    components: { BasicForm },
    props: {
      formData: propTypes.object.def({}),
      formBpm: propTypes.bool.def(true),
    },
    setup(props) {
      const [registerForm, {
        setFieldsValue,
        setProps,
        getFieldsValue,
        validateFields,
        clearValidate
      }] = useForm({
        labelWidth: 150,
        schemas: getBpmFormSchema(props.formData),
        showActionButtonGroup: false,
        baseColProps: {span: 6}
      });

      const formDisabled = computed(() => {
        return props.formData.disabled !== false;
      });

      const refKeys = ref(['recipeDetail']);
      const activeKey = ref('recipeDetail');
      const recipeDetail = ref();
      const tableRefs = {recipeDetail};
      const recipeDetailTable = reactive({
        loading: false,
        dataSource: [],
        columns: recipeDetailColumns,
        show: false
      });

      // 监听占比类型变化
      watch(() => {
        try {
          const values = getFieldsValue();
          return values?.proportionType;
        } catch (e) {
          return undefined;
        }
      }, (newType, oldType) => {
        if (newType !== undefined && newType !== oldType) {
          nextTick(() => {
            calculateProportionTotal();

            // 关键：根据新类型处理校验状态
            if (newType === '1') {
              // 标准类型：触发校验（显示错误）
              validateFields(['proportionTotal']).catch(() => {});
            } else {
              // 特殊类型：清除错误提示
              clearValidate(['proportionTotal']);
            }
          });
        }
      });

      // 计算配比总和
      function calculateProportionTotal() {
        const tableData = recipeDetailTable.dataSource || [];
        let total = 0;
        tableData.forEach(row => {
          total += parseFloat(row.proportion) || 0;
        });

        total = Math.round(total * 100) / 100;

        try {
          const currentValues = getFieldsValue();
          if (currentValues) {
            setFieldsValue({ proportionTotal: total.toString() });
          }
        } catch (e) {
          console.warn('设置配比总和失败:', e);
        }
      }

      // 明细行值变化
      function handleDetailValueChange({ row, column, value }) {
        if (column.key === 'proportion') {
          const index = recipeDetailTable.dataSource.findIndex(item =>
            item.id === row.id || (item._X_ROW_KEY && item._X_ROW_KEY === row._X_ROW_KEY)
          );
          if (index !== -1) {
            recipeDetailTable.dataSource[index].proportion = value;
          }

          calculateProportionTotal();

          // 根据当前类型决定是否校验
          nextTick(() => {
            const currentType = getFieldsValue()?.proportionType;
            if (currentType === '1') {
              validateFields(['proportionTotal']).catch(() => {});
            }
            else{
              // 特殊类型：清除错误提示
              clearValidate(['proportionTotal']);
            }
          });
        }
      }

      // 明细行删除
      function handleDetailDeleted() {
        nextTick(() => {
          calculateProportionTotal();
          const currentType = getFieldsValue()?.proportionType;
          if (currentType === '1') {
            validateFields(['proportionTotal']).catch(() => {});
          }
          else{
            // 特殊类型：清除错误提示
            clearValidate(['proportionTotal']);
             }
        });
      }

      const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
        requestAddOrEdit,
        classifyIntoFormData,
        tableRefs,
        activeKey,
        refKeys,
        validateSubForm
      );

      function classifyIntoFormData(allValues) {
        let main = Object.assign({}, allValues.formValue);
        return {
          ...main,
          recipeDetailList: allValues.tablesValue[0].tableData,
        }
      }

      async function requestAddOrEdit(values) {
        const proportionType = values.proportionType;
        const proportionTotal = parseFloat(values.proportionTotal) || 0;

        // 只有标准类型才校验
        if (proportionType === '1' && proportionTotal !== 100) {
          throw new Error('标准类型的配比总和必须等于100，当前总和为：' + proportionTotal);
        }

        await saveOrUpdate(values, true);
      }

      const queryByIdUrl = '/Recipe/recipe/queryById';
      async function initFormData() {
        let params = {id: props.formData.dataId};
        const data = await defHttp.get({url: queryByIdUrl, params});

        await setFieldsValue({...data});

        requestSubTableData(recipeDetailList, {id: data.id}, recipeDetailTable, () => {
          recipeDetailTable.show = true;
          nextTick(() => {
            calculateProportionTotal();
            // 初始化时根据类型设置校验状态
            if (data.proportionType === '1' || !data.proportionType) {
              validateFields(['proportionTotal']).catch(() => {});
            }
            else {
              clearValidate(['proportionTotal']);
            }
          });
        });

        await setProps({disabled: formDisabled.value});
      }

      initFormData();

      return {
        registerForm,
        formDisabled,
        formRef,
        handleSubmit,
        activeKey,
        handleChangeTabs,
        recipeDetail,
        recipeDetailTable,
        handleDetailValueChange,
        handleDetailDeleted,
      }
    }
  });
</script>
