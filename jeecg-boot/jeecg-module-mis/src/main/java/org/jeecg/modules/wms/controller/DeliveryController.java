package org.jeecg.modules.wms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.scm.entity.SalesOrder;
import org.jeecg.modules.scm.entity.SalesOrderDetail;
import org.jeecg.modules.scm.mapper.SalesOrderDetailMapper;
import org.jeecg.modules.scm.service.ISalesOrderService;
import org.jeecg.modules.wms.vo.*;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.entity.Delivery;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockOut;
import org.jeecg.modules.wms.entity.StockOutDetail;
import org.jeecg.modules.wms.mapper.DeliveryDetailMapper;
import org.jeecg.modules.wms.service.IDeliveryService;
import org.jeecg.modules.wms.service.IDeliveryDetailService;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.service.IStockOutService;
import org.jeecg.modules.wms.service.IStockOutDetailService;
import org.jeecg.modules.wms.service.IWarehouseService;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.wms.entity.Warehouse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Tag(name="发货表")
@RestController
@RequestMapping("/wms/delivery")
@Slf4j
public class DeliveryController {
	@Autowired
	private IDeliveryService deliveryService;
	@Autowired
	private IDeliveryDetailService deliveryDetailService;
	@Autowired
	private DeliveryDetailMapper deliveryDetailMapper;
	@Autowired
	private SalesOrderDetailMapper salesOrderDetailMapper;
	@Autowired
	private ISalesOrderService salesOrderService;
	@Autowired
	private IStockService stockService;
	@Autowired
	private IStockOutService stockOutService;
	@Autowired
	private IStockOutDetailService stockOutDetailService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	private IWarehouseService warehouseService;
	
	/**
	 * 分页列表查询
	 *
	 * @param delivery
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "发货表-分页列表查询")
	@Operation(summary="发货表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Delivery>> queryPageList(Delivery delivery,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("sourceType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("logisticsType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Delivery> queryWrapper = QueryGenerator.initQueryWrapper(delivery, req.getParameterMap(),customeRuleMap);
		Page<Delivery> page = new Page<Delivery>(pageNo, pageSize);
		IPage<Delivery> pageList = deliveryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param deliveryPage
	 * @return
	 */
	@AutoLog(value = "发货表-添加")
	@Operation(summary="发货表-添加")
    @RequiresPermissions("wms:mis_delivery:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DeliveryPage deliveryPage) {
		Delivery delivery = new Delivery();
		BeanUtils.copyProperties(deliveryPage, delivery);
		deliveryService.saveMain(delivery, deliveryPage.getDeliveryDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param deliveryPage
	 * @return
	 */
	@AutoLog(value = "发货表-编辑")
	@Operation(summary="发货表-编辑")
    @RequiresPermissions("wms:mis_delivery:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DeliveryPage deliveryPage) {
		Delivery delivery = new Delivery();
		BeanUtils.copyProperties(deliveryPage, delivery);
		Delivery deliveryEntity = deliveryService.getById(delivery.getId());
		if(deliveryEntity==null) {
			return Result.error("未找到对应数据");
		}
		deliveryService.updateMain(delivery, deliveryPage.getDeliveryDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发货表-通过id删除")
	@Operation(summary="发货表-通过id删除")
    @RequiresPermissions("wms:mis_delivery:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		deliveryService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "发货表-批量删除")
	@Operation(summary="发货表-批量删除")
    @RequiresPermissions("wms:mis_delivery:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.deliveryService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "发货表-通过id查询")
	@Operation(summary="发货表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Delivery> queryById(@RequestParam(name="id",required=true) String id) {
		Delivery delivery = deliveryService.getById(id);
		if(delivery==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(delivery);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "发货明细通过主表ID查询")
	@Operation(summary="发货明细主表ID查询")
	@GetMapping(value = "/queryDeliveryDetailByMainId")
	public Result<List<DeliveryDetail>> queryDeliveryDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<DeliveryDetail> deliveryDetailList = deliveryDetailService.selectByMainId(id);
		return Result.OK(deliveryDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param delivery
    */
    @RequiresPermissions("wms:mis_delivery:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Delivery delivery) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<Delivery> queryWrapper = QueryGenerator.initQueryWrapper(delivery, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<Delivery> deliveryList = deliveryService.list(queryWrapper);

      // Step.3 组装pageList
      List<DeliveryPage> pageList = new ArrayList<DeliveryPage>();
      for (Delivery main : deliveryList) {
          DeliveryPage vo = new DeliveryPage();
          BeanUtils.copyProperties(main, vo);
          List<DeliveryDetail> deliveryDetailList = deliveryDetailService.selectByMainId(main.getId());
          vo.setDeliveryDetailList(deliveryDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "发货表列表");
      mv.addObject(NormalExcelConstants.CLASS, DeliveryPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发货表数据", "导出人:"+sysUser.getRealname(), "发货表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_delivery:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<DeliveryPage> list = ExcelImportUtil.importExcel(file.getInputStream(), DeliveryPage.class, params);
              for (DeliveryPage page : list) {
                  Delivery po = new Delivery();
                  BeanUtils.copyProperties(page, po);
                  deliveryService.saveMain(po, page.getDeliveryDetailList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

/// ===============================发货相关操作 开始==============================================================================
	 /**
	  * 待发货任务列表（订单维度）- 复用原有接口
	  */
	 @GetMapping("/taskList")
	 public Result<IPage<DeliveryTaskVo>> taskList(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 @RequestParam(required = false) String orderNo,
			 @RequestParam(required = false) String customerName,
			 @RequestParam(required = false) String salesmanId,
			 @RequestParam(required = false) String orderDate_begin,
			 @RequestParam(required = false) String orderDate_end,
			 @RequestParam(required = false) String deliveryDate_begin,
			 @RequestParam(required = false) String deliveryDate_end,
			 @RequestParam(required = false) String deliveryStatus,
			 @RequestParam(required = false) String hideCompleted,
			 @RequestParam(required = false) String onlyUrgent,
			 @RequestParam(required = false, defaultValue = "true") Boolean alertEnabled,
			 @RequestParam(required = false, defaultValue = "3") Integer alertDays,
			 @RequestParam(required = false) String column,
			 @RequestParam(required = false) String order) {

		 Page<DeliveryTaskVo> page = new Page<>(pageNo, pageSize);

		 // 组装参数
		 Map<String, Object> param = new HashMap<>();
		 param.put("orderNo", orderNo);
		 param.put("customerName", customerName);
		 param.put("salesmanId", salesmanId);
		 param.put("orderDate_begin", orderDate_begin);
		 param.put("orderDate_end", orderDate_end);
		 param.put("deliveryDate_begin", deliveryDate_begin);
		 param.put("deliveryDate_end", deliveryDate_end);
		 param.put("deliveryStatus", deliveryStatus);
		 param.put("hideCompleted", hideCompleted);
		 param.put("onlyUrgent", onlyUrgent);
		 param.put("alertEnabled", alertEnabled);
		 param.put("alertDays", alertDays);
		 param.put("column", column);
		 param.put("order", order);

		 IPage<DeliveryTaskVo> result = deliveryService.queryTaskList(page, param);
		 return Result.OK(result);
	 }

	 /**
	 * 查询销售订单未发货明细
	 */
	@Operation(summary="查询销售订单未发货明细")
	@GetMapping(value = "/pendingOrderLines")
	public Result<PendingOrderInfoVo> pendingOrderLines(
			@RequestParam(name="orderId",required=true) String orderId) {
		PendingOrderInfoVo result = new PendingOrderInfoVo();

		// 1. 查询订单主表
		SalesOrder order = salesOrderService.getById(orderId);
		result.setOrder(order);

		// 2. 查询未发货明细
		List<SalesOrderDetail> lines = salesOrderDetailMapper.selectByMainId(orderId);
		List<PendingOrderLineVo> lineVos = new ArrayList<>();

		for (SalesOrderDetail line : lines) {
			BigDecimal deliveredQty = deliveryDetailMapper.sumDeliveredQtyBySourceDetailId(line.getId());
			BigDecimal remainingQty = line.getOrderQty().subtract(deliveredQty);

			if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
				PendingOrderLineVo vo = new PendingOrderLineVo();
				BeanUtils.copyProperties(line, vo);
				vo.setDeliveredQty(deliveredQty);
				vo.setRemainingQty(remainingQty);
				lineVos.add(vo);
			}
		}
		result.setLines(lineVos);
		return Result.OK(result);
	}

	/**
	 * 扫码解析库存
	 */
	@Operation(summary="扫码解析库存")
	@PostMapping(value = "/scanCode")
	public Result<ScanStockVo> scanCode(@RequestBody ScanStockVo param) {
		String scanCode = param.getScanCode();
		String orderId = param.getOrderId();
		ScanStockVo result = new ScanStockVo();
		result.setScanCode(scanCode);

		// ========== 解析扫码内容 ==========
		String goodsCode = null;
		String batchNo = null;

		if (scanCode == null || scanCode.trim().isEmpty()) {
			result.setMatched(false);
			result.setMsg("扫码内容为空");
			return Result.OK(result);
		}

		String code = scanCode.trim();
		if (code.startsWith("{")) {
			// ===== 新系统 JSON 格式 =====
			try {
				JSONObject json = JSON.parseObject(code);
				goodsCode = json.getString("p");
				batchNo = json.getString("b");
			} catch (Exception e) {
				result.setMatched(false);
				result.setMsg("二维码格式错误");
				return Result.OK(result);
			}
		} else if (code.contains(";")) {
			// ===== 老系统 ; 分隔格式 =====
			// 格式：orderNo;productNo;batchNo;productDate;deliverDate;singleWeight
			String[] parts = code.split(";");
			if (parts.length >= 3) {
				// parts[0] = orderNo（订单号，忽略）
				// parts[1] = productNo（产品编码）
				// parts[2] = batchNo（批次号）
				goodsCode = parts[1];
				batchNo = parts[2];
			}
		}

		// 解析扫码内容：支持 goodsCode 或 goodsCode|batchNo 或 goodsCode|batchNo|stockId
//		String goodsCode = scanCode;
//		String batchNo = null;
//		if (scanCode != null && scanCode.contains("|")) {
//			String[] parts = scanCode.split("\\|");
//			goodsCode = parts[0];
//			if (parts.length > 1) {
//				batchNo = parts[1];
//			}
//		}
       // 校验
		if (oConvertUtils.isEmpty(goodsCode)) {
			result.setMatched(false);
			result.setMsg("二维码缺少产品编码");
			return Result.OK(result);
		}

		result.setGoodsCode(goodsCode);
		result.setBatchNo(batchNo);

		// 1. 查询订单未发货明细
		List<PendingOrderLineVo> pendingLines = new ArrayList<>();
		List<SalesOrderDetail> lines = salesOrderDetailMapper.selectByMainId(orderId);
		for (SalesOrderDetail line : lines) {
			BigDecimal deliveredQty = deliveryDetailMapper.sumDeliveredQtyBySourceDetailId(line.getId());
			BigDecimal remainingQty = line.getOrderQty().subtract(deliveredQty);
			if (remainingQty.compareTo(BigDecimal.ZERO) > 0 && goodsCode.equals(line.getProductCode())) {
				PendingOrderLineVo vo = new PendingOrderLineVo();
				BeanUtils.copyProperties(line, vo);
				vo.setDeliveredQty(deliveredQty);
				vo.setRemainingQty(remainingQty);
				pendingLines.add(vo);
			}
		}

		if (pendingLines.isEmpty()) {
			result.setMatched(false);
			result.setMsg("该产品不在当前订单中或已发完");
			return Result.OK(result);
		}

		// 2. 查询FIFO可用库存（不按仓库限制）
		QueryWrapper<Stock> qw = new QueryWrapper<>();
		qw.eq("goods_code", goodsCode);
		qw.eq("del_flag", "0");
		qw.apply("(quantity - locked_qty) > 0");
		if (oConvertUtils.isNotEmpty(batchNo)) {
			qw.eq("batch_no", batchNo);
		}
		qw.orderByAsc("stock_in_time", "create_time");
		List<Stock> stocks = stockService.list(qw);

		if (stocks.isEmpty()) {
			result.setMatched(false);
			result.setMsg("该产品无可用库存");
			return Result.OK(result);
		}

		result.setMatched(true);
		result.setMsg("匹配成功，请选择库存");
		result.setOrderLines(pendingLines);
		result.setStocks(stocks);
		return Result.OK(result);
	}

	/**
	 * 扫码发货（生成发货单+出库单+扣减库存）
	 */
	@AutoLog(value = "扫码发货")
	@Operation(summary="扫码发货")
	@RequiresPermissions("wms:mis_delivery:add")
	@PostMapping(value = "/scanDeliver")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> scanDeliver(@RequestBody ScanDeliveryRequestVo request) {
		if (request.getScanItems() == null || request.getScanItems().isEmpty()) {
			return Result.error("发货明细不能为空");
		}

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Date now = new Date();

		// ===== 1. 生成并保存出库单 =====
		StockOut stockOut = new StockOut();
		stockOut.setStockOutNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.STOCK_OUT.getPrefix()));
		stockOut.setStockOutType(StockEnum.StockOutType.SALES.getCode());
		stockOut.setSourceOrderId(request.getSourceOrderId());
		stockOut.setSourceOrderCode(request.getSourceOrderNo());
		stockOut.setCustomerId(request.getCustomerId());
		stockOut.setCustomerName(request.getCustomerName());
		stockOut.setOperatorUserId(loginUser.getId());
		stockOut.setOperatorName(loginUser.getRealname());
		stockOut.setStatus(StockEnum.StockOutStatus.FINISHED.getCode());
		stockOut.setApproveStatus("1");
		stockOut.setApproveId(loginUser.getId());
		stockOut.setApproveName(loginUser.getRealname());
		stockOut.setApproveTime(now);
		stockOut.setApplyTime(now);
		stockOut.setStockOutTime(now);
		stockOut.setConsignee(request.getConsignee());
		stockOut.setConsigneePhone(request.getConsigneePhone());
		stockOut.setDeliverAddress(request.getConsigneeAddress());
		stockOut.setRemark(request.getRemark());
		stockOut.setIsProduct("1");
		stockOutService.save(stockOut);

		// ===== 2. 保存出库明细并扣减库存 =====
		List<StockOutDetail> stockOutDetails = new ArrayList<>();
		for (ScanDeliveryItemVo item : request.getScanItems()) {
			// 扣减库存
			stockService.directDeduct(item.getStockId(), item.getActualQty());

			StockOutDetail detail = new StockOutDetail();
			detail.setStockOutId(stockOut.getId());
			detail.setStockOutNo(stockOut.getStockOutNo());
			detail.setGoodsId(item.getGoodsId());
			detail.setGoodsCode(item.getGoodsCode());
			detail.setGoodsName(item.getGoodsName());
			detail.setGoodsSpec(item.getGoodsSpec());
			detail.setUnit(item.getUnit());
			detail.setApplyQty(item.getActualQty());
			detail.setActualQty(item.getActualQty());
			detail.setStockId(item.getStockId());
			detail.setProductionBatchId(item.getProductionBatchId());
			detail.setProductionBatchNo(item.getProductionBatchNo());
			detail.setProductionDate(item.getProductionDate());
			detail.setExpiryDate(item.getExpiryDate());
			detail.setSourceDetailId(item.getSourceDetailId());
			detail.setSourceType("NORMAL");
			detail.setOverFlag("0");
			if (item.getUnitPrice() != null) {
				detail.setSalesPrice(item.getUnitPrice());
				detail.setSalesTotal(item.getUnitPrice().multiply(item.getActualQty()).setScale(2, RoundingMode.HALF_UP));
			}
			// 设置批次号与仓库
			detail.setBatchNo(item.getProductionBatchNo());
			if (oConvertUtils.isNotEmpty(item.getWarehouseId())) {
				Warehouse wh = warehouseService.getById(item.getWarehouseId());
				if (wh != null) {
					stockOut.setWarehouseId(item.getWarehouseId());
					stockOut.setWarehouseName(wh.getName());
				}
			}
			stockOutDetailService.save(detail);
			stockOutDetails.add(detail);
		}
		// 更新出库单仓库（取第一条明细的仓库）
		stockOutService.updateById(stockOut);

		// ===== 3. 生成并保存发货单 =====
		Delivery delivery = new Delivery();
		delivery.setDeliveryNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.DELIVERY_NOTE_ORDER.getPrefix()));
		delivery.setSourceType("SALES");
		delivery.setSourceOrderId(request.getSourceOrderId());
		delivery.setSourceOrderNo(request.getSourceOrderNo());
		delivery.setCustomerId(request.getCustomerId());
		delivery.setCustomerName(request.getCustomerName());
		delivery.setConsignee(request.getConsignee());
		delivery.setConsigneePhone(request.getConsigneePhone());
		delivery.setConsigneeAddress(request.getConsigneeAddress());
		delivery.setLogisticsType(request.getLogisticsType());
		delivery.setLogisticsCompanyId(request.getLogisticsCompanyId());
		delivery.setLogisticsCompanyCode(request.getLogisticsCompanyCode());
		delivery.setLogisticsCompany(request.getLogisticsCompany());
		delivery.setLogisticsNo(request.getLogisticsNo());
		delivery.setLogisticsCost(request.getLogisticsCost());
		delivery.setDriverPhone(request.getDriverPhone());
		delivery.setDeliveryTime(request.getDeliveryTime());
		delivery.setStatus("FINISHED");
		delivery.setStockOutId(stockOut.getId());
		delivery.setStockOutNo(stockOut.getStockOutNo());
		delivery.setRemark(request.getRemark());
		delivery.setDeliverBy(loginUser.getRealname());

		// 计算总数量和金额
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		List<DeliveryDetail> deliveryDetails = new ArrayList<>();
		for (int i = 0; i < request.getScanItems().size(); i++) {
			ScanDeliveryItemVo item = request.getScanItems().get(i);
			StockOutDetail outDetail = stockOutDetails.get(i);

			DeliveryDetail detail = new DeliveryDetail();
			detail.setSourceDetailId(item.getSourceDetailId());
			detail.setGoodsId(item.getGoodsId());
			detail.setGoodsCode(item.getGoodsCode());
			detail.setGoodsName(item.getGoodsName());
			detail.setGoodsSpec(item.getGoodsSpec());
			detail.setUnit(item.getUnit());
			detail.setProductionBatchId(item.getProductionBatchId());
			detail.setProductionBatchNo(item.getProductionBatchNo());
			detail.setProductionDate(item.getProductionDate());
			detail.setExpiryDate(item.getExpiryDate());
			detail.setStockId(item.getStockId());
			detail.setWarehouseId(item.getWarehouseId());
			detail.setWarehouseName(item.getWarehouseName());
			detail.setActualQty(item.getActualQty());
			detail.setUnitPrice(item.getUnitPrice());
			if (item.getUnitPrice() != null && item.getActualQty() != null) {
				BigDecimal lineAmt = item.getUnitPrice().multiply(item.getActualQty()).setScale(2, RoundingMode.HALF_UP);
				detail.setDetailAmount(lineAmt);
				totalAmount = totalAmount.add(lineAmt);
			}
			detail.setScanCode(item.getScanCode());
			detail.setScanTime(now);
			detail.setStockOutDetailId(outDetail.getId());
			detail.setRemark(item.getRemark());
			deliveryDetails.add(detail);

			totalQty = totalQty.add(item.getActualQty());
		}
		delivery.setDeliveryQty(totalQty);
		delivery.setDeliveryAmount(totalAmount);

		deliveryService.saveMain(delivery, deliveryDetails);

		return Result.OK("扫码发货成功，已生成发货单" + delivery.getDeliveryNo() + "和出库单" + stockOut.getStockOutNo());
	}

	 /**
	  * 根据订单ID查询所有未发货产品的FIFO库存
	  */
	 @Operation(summary = "查询订单可用库存")
	 @GetMapping(value = "/orderStocks")
	 public Result<List<Stock>> orderStocks(
			 @RequestParam(name = "orderId", required = true) String orderId) {

		 // 1. 查询订单未发货明细的产品编码
		 List<SalesOrderDetail> lines = salesOrderDetailMapper.selectByMainId(orderId);
		 List<String> productCodes = new ArrayList<>();

		 for (SalesOrderDetail line : lines) {
			 BigDecimal deliveredQty = deliveryDetailMapper.sumDeliveredQtyBySourceDetailId(line.getId());
			 BigDecimal remainingQty = line.getOrderQty().subtract(deliveredQty);
			 if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
				 productCodes.add(line.getProductCode());
			 }
		 }

		 if (productCodes.isEmpty()) {
			 return Result.OK(new ArrayList<>());
		 }

		 // 2. 查询这些产品的可用库存（FIFO）
		 QueryWrapper<Stock> qw = new QueryWrapper<>();
		 qw.in("goods_code", productCodes);
		 qw.eq("del_flag", "0");
		 qw.apply("(quantity - locked_qty) > 0");
		 qw.orderByAsc("stock_in_time", "create_time");

		 List<Stock> stocks = stockService.list(qw);
		 return Result.OK(stocks);
	 }

}
