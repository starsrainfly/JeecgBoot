<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="`质检录入 - ${taskInfo?.batchNo}`"
    :width="1000"
    :canFullscreen="true"
    @ok="handleComplete"
  >
    <a-alert message="来源工单信息" :description="sourceTaskInfo" type="info" class="mb-4" />

    <a-form :model="formData" layout="vertical">
      <a-form-item label="质检结果">
        <a-radio-group v-model:value="formData.qcResult">
          <a-radio-button value="pass">合格</a-radio-button>
          <a-radio-button value="fail">不合格</a-radio-button>
          <a-radio-button value="rework">返工</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="质检项目">
        <a-table :columns="qcItemColumns" :dataSource="qcItems" size="small">
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'actualValue'">
              <a-input v-model:value="record.actualValue" />
            </template>
            <template v-if="column.dataIndex === 'result'">
              <a-select v-model:value="record.result">
                <a-select-option value="pass">合格</a-select-option>
                <a-select-option value="fail">不合格</a-select-option>
              </a-select>
            </template>
          </template>
        </a-table>
      </a-form-item>

      <a-form-item label="质检结论">
        <a-textarea v-model:value="formData.qcConclusion" :rows="3" />
      </a-form-item>
    </a-form>
  </BasicModal>
</template>
