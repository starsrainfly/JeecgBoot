package org.jeecg.modules.wms.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.wms.entity.Warehouse;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.service.IWarehouseService;
import org.jeecg.modules.wms.vo.StockOutDetailVo;
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
import org.jeecg.modules.wms.entity.StockOutDetail;
import org.jeecg.modules.wms.entity.StockOut;
import org.jeecg.modules.wms.vo.StockOutPage;
import org.jeecg.modules.wms.service.IStockOutService;
import org.jeecg.modules.wms.service.IStockOutDetailService;
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
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
@Tag(name="出库表")
@RestController
@RequestMapping("/wms/stockOut")
@Slf4j
public class StockOutController {
	@Autowired
	private IStockOutService stockOutService;
	@Autowired
	private IStockOutDetailService stockOutDetailService;
	@Autowired
	private ISerialNoService serialNoService;
	 @Autowired
	 private ISysUserService userService;
	 @Autowired
	 private IWarehouseService warehouseService;
	 @Autowired
	 private IStockService stockService;
	/**
	 * 分页列表查询
	 *
	 * @param stockOut
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "出库表-分页列表查询")
	@Operation(summary="出库表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockOut>> queryPageList(StockOut stockOut,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("stockOutType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("requesterUserId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("approveStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<StockOut> queryWrapper = QueryGenerator.initQueryWrapper(stockOut, req.getParameterMap(),customeRuleMap);
		Page<StockOut> page = new Page<StockOut>(pageNo, pageSize);
		IPage<StockOut> pageList = stockOutService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 查询库存占用情况（通过IStockService）
	  */
	 @Operation(summary = "查询库存占用情况")
	 @GetMapping(value = "/stockOccupancy")
	 public Result<Map<String, Object>> getStockOccupancy(@RequestParam String warehouseId, @RequestParam String goodsId) {
		 Map<String, Object> result = stockService.selectStockOccupancy(warehouseId, goodsId);
		 return Result.OK(result);
	 }
	 /**
	  *   添加
	  *
	  * @param stockOutPage
	  * @return
	  */
	 @AutoLog(value = "出库表-申请")
	 @Operation(summary="出库表-申请")
	 @RequiresPermissions("wms:mis_stock_out:add")
	 @PostMapping(value = "/apply")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> add(@RequestBody StockOutPage stockOutPage) {
		 // 1. 构建主表
		 StockOut stockOut = buildStockOut(stockOutPage);
		 stockOut.setStatus(StockEnum.StockOutStatus.APPLY.getCode());
		 stockOut.setApproveStatus(ApproveStatusEnum.PENDING.getCode());

		 // 2. 【核心】FIFO匹配库存并锁定 - 调用IStockOutService
		 List<StockOutDetail> matchedDetails = stockOutService.matchAndLockStock(
				 stockOut,
				 stockOutPage.getStockOutDetailList()
		 );

		 // 3. 保存主表和明细 及更新物料需求表相关数量
		 stockOutService.applyStockOut(stockOut, matchedDetails);

		 return Result.OK("出库申请提交成功，已锁定库存！");
	 }
	 /**
	  * 审核通过 - 确认出库
	  */
	 @AutoLog(value = "出库表-审核通过")
	 @Operation(summary = "出库表-审核通过")
	 @PostMapping(value = "/approve")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> approve(@RequestBody StockOutPage stockOutPage) {
		 StockOut stockOut = buildStockOut(stockOutPage);
		 StockOut stockOutEntity = stockOutService.getById(stockOutPage.getId());
		 if (stockOutEntity == null) {
			 return Result.error("出库单不存在");
		 }
		 if (!"APPLY".equals(stockOutEntity.getStatus())) {
			 return Result.error("只有申请状态的出库单可以审核");
		 }
		 stockOutEntity.setApproveRemark(stockOutPage.getApproveRemark());
		 stockOutEntity.setApproveStatus(stockOutPage.getApproveStatus());
		 stockOutEntity.setRemark(stockOutPage.getRemark());

		 if(stockOut.getApproveStatus().equals(ApproveStatusEnum.PASS.getCode())) {
			 stockOutService.approveStockOut(stockOutEntity, stockOutPage.getStockOutDetailList());
		 }
		 else if(stockOut.getApproveStatus().equals(ApproveStatusEnum.REJECT.getCode())) {
			 stockOutService.rejectStockOut(stockOut);
			 return Result.OK("已驳回，库存锁定已释放");
		 }
		 return Result.OK("审核通过，库存已出库！");
	 }

	 /**
	  * 审核驳回
	  */
	 @AutoLog(value = "出库表-审核驳回")
	 @Operation(summary = "出库表-审核驳回")
	 @PostMapping(value = "/reject")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> reject(@RequestBody StockOutPage stockOutPage) {

		 StockOut stockOutEntity = stockOutService.getById(stockOutPage.getId());
		 if (stockOutEntity == null) {
			 return Result.error("出库单不存在");
		 }

		 if (!"APPLY".equals(stockOutEntity.getStatus())) {
			 return Result.error("只有申请状态的出库单可以驳回");
		 }
		 stockOutEntity.setApproveRemark(stockOutPage.getApproveRemark());
		 stockOutEntity.setApproveStatus(ApproveStatusEnum.REJECT.getCode());
		 stockOutEntity.setRemark(stockOutPage.getRemark());
		 stockOutService.rejectStockOut(stockOutEntity);


		 return Result.OK("已驳回，库存锁定已释放");
	 }

	 /**
	  * 取消申请
	  */
	 @AutoLog(value = "出库表-取消")
	 @Operation(summary = "出库表-取消")
	 @RequestMapping(value = "/cancel", method = {RequestMethod.PUT,RequestMethod.POST})
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> cancel(@RequestParam(name="id",required=true)  String id) {
		 StockOut stockOut = stockOutService.getById(id);
		 if (stockOut == null) {
			 return Result.error("出库单不存在");
		 }
		 if (!"APPLY".equals(stockOut.getStatus())) {
			 return Result.error("只有申请状态的出库单可以取消");
		 }
		 stockOut.setApproveRemark(stockOut.getApproveRemark() + "主动取消");
		 stockOutService.rejectStockOut(stockOut);
//		 // 释放锁定
//		 stockOutService.releaseStockLock(id);
//		 // 更新状态
//		 stockOut.setRemark(stockOut.getRemark() + "主动取消");
//		 stockOut.setStatus(StockEnum.StockOutStatus.CANCEL.getCode());
//		 stockOutService.updateById(stockOut);
		 return Result.OK("已取消，库存锁定已释放");
	 }


	 /**
	  * 组合出库主表中的一些基础信息
	  * @param page
	  * @return
	  */
	 private StockOut buildStockOut(StockOutPage page) {
		 StockOut stockOut = new StockOut();
		 BeanUtils.copyProperties(page, stockOut);
		 stockOut.setStockOutNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.STOCK_OUT.getPrefix()));
		 stockOut.setApplyTime(new DateTime());

		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 stockOut.setOperatorUserId(loginUser.getId());
		 stockOut.setOperatorName(loginUser.getRealname());

		 if (StringUtils.isNotBlank(stockOut.getRequesterUserId())) {
			 SysUser sysUser = userService.getById(stockOut.getRequesterUserId());
			 if (sysUser != null) {
				 stockOut.setRequesterName(sysUser.getRealname());
			 }
		 }
		 if (StringUtils.isNotBlank(stockOut.getWarehouseId())) {
			 Warehouse warehouse = warehouseService.getById(stockOut.getWarehouseId());
			 if (warehouse != null) {
				 stockOut.setWarehouseName(warehouse.getName());
			 }
		 }
		 return stockOut;

	 }
	/**
	 *  编辑
	 *
	 * @param stockOutPage
	 * @return
	 */
	@AutoLog(value = "出库表-编辑")
	@Operation(summary="出库表-编辑")
    @RequiresPermissions("wms:mis_stock_out:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StockOutPage stockOutPage) {
		StockOut stockOut = new StockOut();
		BeanUtils.copyProperties(stockOutPage, stockOut);
		StockOut stockOutEntity = stockOutService.getById(stockOut.getId());
		if(stockOutEntity==null) {
			return Result.error("未找到对应数据");
		}
		// 只有申请状态可以编辑
		if (!"APPLY".equals(stockOut.getStatus())) {
			return Result.error("只有申请状态的出库单可以编辑");
		}
		// 1. 【关键】先释放原锁定库存
		stockOutService.releaseStockLock(stockOut.getId());
		// 2. 【关键】重新匹配库存并锁定（新的明细）
		List<StockOutDetail> matchedDetails = stockOutService.matchAndLockStock(
				stockOut,
				stockOutPage.getStockOutDetailList()
		);
		// 3. 更新主表和明细（先删旧明细，再插新明细）
		stockOutService.updateMain(stockOut, matchedDetails);
		return Result.OK("编辑成功!");
	}


	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "出库表-通过id删除")
	@Operation(summary="出库表-通过id删除")
    @RequiresPermissions("wms:mis_stock_out:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {

		StockOut stockOut = stockOutService.getById(id);
		if (stockOut == null) {
			return Result.error("未找到对应数据");
		}
		if (!"APPLY".equals(stockOut.getStatus())) {
			return Result.error("只有申请状态的出库单可以删除");
		}
		stockOutService.deleteStockOut(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "出库表-批量删除")
	@Operation(summary="出库表-批量删除")
    @RequiresPermissions("wms:mis_stock_out:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stockOutService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出库表-通过id查询")
	@Operation(summary="出库表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StockOut> queryById(@RequestParam(name="id",required=true) String id) {
		StockOut stockOut = stockOutService.getById(id);
		if(stockOut==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stockOut);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出库明细表通过主表ID查询")
	@Operation(summary="出库明细表主表ID查询")
	@GetMapping(value = "/queryStockOutDetailByMainId")
	public Result<List<StockOutDetail>> queryStockOutDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<StockOutDetail> stockOutDetailList = stockOutDetailService.selectByMainId(id);
		return Result.OK(stockOutDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stockOut
    */
    @RequiresPermissions("wms:mis_stock_out:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockOut stockOut) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<StockOut> queryWrapper = QueryGenerator.initQueryWrapper(stockOut, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<StockOut> stockOutList = stockOutService.list(queryWrapper);

      // Step.3 组装pageList
      List<StockOutPage> pageList = new ArrayList<StockOutPage>();
      for (StockOut main : stockOutList) {
          StockOutPage vo = new StockOutPage();
          BeanUtils.copyProperties(main, vo);
          List<StockOutDetail> stockOutDetailList = stockOutDetailService.selectByMainId(main.getId());
          vo.setStockOutDetailList(stockOutDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "出库表列表");
      mv.addObject(NormalExcelConstants.CLASS, StockOutPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库表数据", "导出人:"+sysUser.getRealname(), "出库表"));
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
    @RequiresPermissions("wms:mis_stock_out:importExcel")
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
              List<StockOutPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StockOutPage.class, params);
              for (StockOutPage page : list) {
                  StockOut po = new StockOut();
                  BeanUtils.copyProperties(page, po);
                  stockOutService.saveMain(po, page.getStockOutDetailList());
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

	 /**
	  * 出库明细查询 — 分页列表（带合计）
	  */
	 @AutoLog(value = "出库明细查询-分页列表")
	 @ApiOperation(value = "出库明细查询-分页列表", notes = "返回列表+合计数据")
	 @GetMapping(value = "/listDetailAll")
	 public Result<IPage<StockOutDetailVo>> queryPageList(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 StockOutDetailVo stockOutDetailVo) {

		 Page<StockOutDetailVo> page = new Page<>(pageNo, pageSize);
		 IPage<StockOutDetailVo> pageList = stockOutDetailService.listDetailAll(page, stockOutDetailVo);
//		 StockOutDetailVo summary = stockOutDetailService.calcDetailTotal(stockOutDetailVo);
//
//		 Map<String, Object> result = new HashMap<>();
//		 result.put("records", pageList.getRecords());
//		 result.put("total", pageList.getTotal());
//		 result.put("summary", summary);

		 return Result.OK(pageList);
	 }

	 /**
	  * 出库明细查询 — 导出Excel（带合计行）
	  */
	 @AutoLog(value = "出库明细查询-导出")
	 @ApiOperation(value = "出库明细查询-导出", notes = "出库明细查询-导出")
	 @GetMapping(value = "/exportStockOutDetailXls")
	 public ModelAndView exportDetailAllXls(StockOutDetailVo stockOutDetailVo) {
		 List<StockOutDetailVo> list = stockOutDetailService.listDetailAll(stockOutDetailVo);
//		 StockOutDetailVo summary = stockOutDetailService.calcDetailTotal(stockOutDetailVo);
//
//		 if (summary != null && summary.getApplyQty() != null) {
//			 summary.setGoodsName("合计");
//			 summary.setGoodsCode("");
//			 summary.setStockOutNo("");
//			 list.add(summary);
//		 }
		 // Java Stream 算合计（和入库一样）
		 BigDecimal totalApplyQty = list.stream()
				 .map(StockOutDetailVo::getApplyQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 BigDecimal totalActualQty = list.stream()
				 .map(StockOutDetailVo::getActualQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 BigDecimal totalCostTotal = list.stream()
				 .map(StockOutDetailVo::getCostTotal)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 BigDecimal totalSalesTotal = list.stream()
				 .map(StockOutDetailVo::getSalesTotal)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 BigDecimal totalOverQty = list.stream()
				 .map(StockOutDetailVo::getOverQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 // 构造合计行
		 StockOutDetailVo summary = new StockOutDetailVo();
		 summary.setStockOutNo("");
		 summary.setGoodsName("合计");
		 summary.setGoodsCode("");
		 summary.setApplyQty(totalApplyQty);
		 summary.setActualQty(totalActualQty);
		 summary.setCostTotal(totalCostTotal);
		 summary.setSalesTotal(totalSalesTotal);
		 summary.setOverQty(totalOverQty);
		 list.add(summary);
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "出库明细表");
		 mv.addObject(NormalExcelConstants.CLASS, StockOutDetailVo.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库明细表报表", "出库明细"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, list);
		 return mv;
	 }

}
