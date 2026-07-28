package org.jeecg.modules.scm.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
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
import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import org.jeecg.modules.scm.entity.PurchaseOrder;
import org.jeecg.modules.scm.vo.PurchaseOrderPage;
import org.jeecg.modules.scm.service.IPurchaseOrderService;
import org.jeecg.modules.scm.service.IPurchaseOrderDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description: 采购订单
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
@Tag(name="采购订单")
@RestController
@RequestMapping("/scm/purchaseOrder")
@Slf4j
public class PurchaseOrderController {
	@Autowired
	private IPurchaseOrderService purchaseOrderService;
	@Autowired
	private IPurchaseOrderDetailService purchaseOrderDetailService;

	 @Autowired
	 private ISerialNoService serialNoService;
	 @Autowired
	 private ISysUserService sysUserService;

	/**
	 * 分页列表查询
	 *
	 * @param purchaseOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "采购订单-分页列表查询")
	@Operation(summary="采购订单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PurchaseOrder>> queryPageList(PurchaseOrder purchaseOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<PurchaseOrder> queryWrapper = QueryGenerator.initQueryWrapper(purchaseOrder, req.getParameterMap());
		Page<PurchaseOrder> page = new Page<PurchaseOrder>(pageNo, pageSize);
		IPage<PurchaseOrder> pageList = purchaseOrderService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param purchaseOrderPage
	 * @return
	 */
	@AutoLog(value = "采购订单-添加")
	@Operation(summary="采购订单-添加")
    @RequiresPermissions("scm:mis_purchase_order:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PurchaseOrderPage purchaseOrderPage) {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		BeanUtils.copyProperties(purchaseOrderPage, purchaseOrder);
		// 生成采购单号
		purchaseOrder.setOrderNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.PURCHASE_ORDER.getPrefix()));
// 采购员姓名兜底
		SysUser sysUser = sysUserService.getById(purchaseOrder.getPurchaserId());
		if (sysUser != null) {
			purchaseOrder.setPurchaserName(sysUser.getRealname());
		}
		if (purchaseOrder.getOrderDate() == null) {
			purchaseOrder.setOrderDate(new Date());
		}
		purchaseOrder.setDelFlag("0");
		purchaseOrder.setApproveStatus("0");

		purchaseOrderService.saveMain(purchaseOrder, purchaseOrderPage.getPurchaseOrderDetailList());
		return Result.OK("添加成功！");
	}

	 @AutoLog(value = "采购订单-审核")
	 @Operation(summary="采购订单-审核")
	 @RequiresPermissions("scm:mis_purchase_order:approve")
	 @PostMapping(value = "/approve")
	 public Result<String> approve(@RequestBody PurchaseOrder purchaseOrder) {
		 purchaseOrderService.approve(purchaseOrder);
		 return Result.OK("审核完成！");
	 }

	/**
	 *  编辑
	 *
	 * @param purchaseOrderPage
	 * @return
	 */
	@AutoLog(value = "采购订单-编辑")
	@Operation(summary="采购订单-编辑")
    @RequiresPermissions("scm:mis_purchase_order:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PurchaseOrderPage purchaseOrderPage) {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		BeanUtils.copyProperties(purchaseOrderPage, purchaseOrder);
		PurchaseOrder purchaseOrderEntity = purchaseOrderService.getById(purchaseOrder.getId());
		if(purchaseOrderEntity==null) {
			return Result.error("未找到对应数据");
		}
		purchaseOrderService.updateMain(purchaseOrder, purchaseOrderPage.getPurchaseOrderDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "采购订单-通过id删除")
	@Operation(summary="采购订单-通过id删除")
    @RequiresPermissions("scm:mis_purchase_order:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		purchaseOrderService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "采购订单-批量删除")
	@Operation(summary="采购订单-批量删除")
    @RequiresPermissions("scm:mis_purchase_order:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.purchaseOrderService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "采购订单-通过id查询")
	@Operation(summary="采购订单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PurchaseOrder> queryById(@RequestParam(name="id",required=true) String id) {
		PurchaseOrder purchaseOrder = purchaseOrderService.getById(id);
		if(purchaseOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(purchaseOrder);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "采购明细通过主表ID查询")
	@Operation(summary="采购明细主表ID查询")
	@GetMapping(value = "/queryPurchaseOrderDetailByMainId")
	public Result<List<PurchaseOrderDetail>> queryPurchaseOrderDetailListByMainId(@RequestParam(name="id",required=true) String id) {
//		List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDetailService.selectByMainId(id);
//
//		for (PurchaseOrderDetail d : purchaseOrderDetailList) {
//			BigDecimal received = d.getReceivedQty() == null ? BigDecimal.ZERO : d.getReceivedQty();
//			d.setRemainingQty(d.getOrderQty() == null ? null : d.getOrderQty().subtract(received));
//		}
//
//		return Result.OK(purchaseOrderDetailList);
		List<PurchaseOrderDetail> detailList = purchaseOrderDetailService.selectByMainId(id);
		// 查询在途申请数量（待审核的入库申请）
		Map<String, BigDecimal> appliedMap = new HashMap<>();
		for (Map<String, Object> row : purchaseOrderDetailService.selectAppliedQtyByOrderId(id)) {
			appliedMap.put((String) row.get("sourceDetailId"), new BigDecimal(row.get("appliedQty").toString()));
		}
		for (PurchaseOrderDetail d : detailList) {
			BigDecimal received = d.getReceivedQty() == null ? BigDecimal.ZERO : d.getReceivedQty();
			BigDecimal applied = appliedMap.getOrDefault(d.getId(), BigDecimal.ZERO);
			d.setAppliedQty(applied);
			// 剩余可到货 = 采购量 - 已入库 - 在途申请
			d.setRemainingQty(d.getOrderQty() == null ? null
					: d.getOrderQty().subtract(received).subtract(applied));
		}
		return Result.OK(detailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param purchaseOrder
    */
    @RequiresPermissions("scm:mis_purchase_order:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PurchaseOrder purchaseOrder) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<PurchaseOrder> queryWrapper = QueryGenerator.initQueryWrapper(purchaseOrder, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<PurchaseOrder> purchaseOrderList = purchaseOrderService.list(queryWrapper);

      // Step.3 组装pageList
      List<PurchaseOrderPage> pageList = new ArrayList<PurchaseOrderPage>();
      for (PurchaseOrder main : purchaseOrderList) {
          PurchaseOrderPage vo = new PurchaseOrderPage();
          BeanUtils.copyProperties(main, vo);
          List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDetailService.selectByMainId(main.getId());
          vo.setPurchaseOrderDetailList(purchaseOrderDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "采购订单列表");
      mv.addObject(NormalExcelConstants.CLASS, PurchaseOrderPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("采购订单数据", "导出人:"+sysUser.getRealname(), "采购订单"));
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
    @RequiresPermissions("scm:mis_purchase_order:importExcel")
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
              List<PurchaseOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), PurchaseOrderPage.class, params);
              for (PurchaseOrderPage page : list) {
                  PurchaseOrder po = new PurchaseOrder();
                  BeanUtils.copyProperties(page, po);
                  purchaseOrderService.saveMain(po, page.getPurchaseOrderDetailList());
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

}
