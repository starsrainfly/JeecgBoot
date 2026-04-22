package org.jeecg.modules.scm.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SalesOrderStatusEnum;
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
import org.jeecg.modules.scm.entity.SalesOrderDetail;
import org.jeecg.modules.scm.entity.SalesOrder;
import org.jeecg.modules.scm.vo.SalesOrderPage;
import org.jeecg.modules.scm.service.ISalesOrderService;
import org.jeecg.modules.scm.service.ISalesOrderDetailService;
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
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
@Tag(name="销售订单主表")
@RestController
@RequestMapping("/scm/salesOrder")
@Slf4j
public class SalesOrderController {
	@Autowired
	private ISalesOrderService salesOrderService;
	@Autowired
	private ISalesOrderDetailService salesOrderDetailService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	private ISysUserService userService;
	/**
	 * 分页列表查询
	 *
	 * @param salesOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "销售订单主表-分页列表查询")
	@Operation(summary="销售订单主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SalesOrder>> queryPageList(SalesOrder salesOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("salesmanId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<SalesOrder> queryWrapper = QueryGenerator.initQueryWrapper(salesOrder, req.getParameterMap(),customeRuleMap);
		Page<SalesOrder> page = new Page<SalesOrder>(pageNo, pageSize);
		IPage<SalesOrder> pageList = salesOrderService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param salesOrderPage
	 * @return
	 */
	@AutoLog(value = "销售订单主表-添加")
	@Operation(summary="销售订单主表-添加")
    @RequiresPermissions("scm:mis_sales_order:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SalesOrderPage salesOrderPage) {
		SalesOrder salesOrder = new SalesOrder();
		BeanUtils.copyProperties(salesOrderPage, salesOrder);
		String orderNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.SALES_ORDER.getPrefix());
		salesOrder.setOrderNo(orderNo);
		SysUser salesman = userService.getById(salesOrder.getSalesmanId());
		if(salesman != null) {
			salesOrder.setSalesmanName(salesman.getRealname());
		}
		salesOrder.setOrderStatus(SalesOrderStatusEnum.APPROVE.getCode());
		salesOrderService.saveMain(salesOrder, salesOrderPage.getSalesOrderDetailList());
		return Result.OK("添加成功！");
	}
	 @AutoLog(value = "销售订单主表-业务审核")
	 @Operation(summary="销售订单主表-业务审核")
	 @RequestMapping(value = "/salesApprove", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> salesApprove(@RequestBody SalesOrderPage salesOrderPage) {
		 SalesOrder salesOrder = new SalesOrder();
		 BeanUtils.copyProperties(salesOrderPage, salesOrder);
		 SalesOrder salesOrderEntity = salesOrderService.getById(salesOrder.getId());
		 if(salesOrderEntity==null) {
			 return Result.error("未找到对应数据");
		 }

		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 salesOrder.setSalesApproverId(loginUser.getId());
		 salesOrder.setSalesApproverName(loginUser.getRealname());
		 salesOrder.setSalesApproveTime(new DateTime());
         salesOrder.setOrderStatus(SalesOrderStatusEnum.APPROVE.getCode());
		 salesOrderService.updateById(salesOrder);
		 return Result.OK("业务审核成功!");
	}
	 @AutoLog(value = "销售订单主表-财务审核")
	 @Operation(summary="销售订单主表-财务审核")
	 @RequestMapping(value = "/financeApprove", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> financeApprove(@RequestBody SalesOrderPage salesOrderPage) {
		 SalesOrder salesOrder = new SalesOrder();
		 BeanUtils.copyProperties(salesOrderPage, salesOrder);
		 SalesOrder salesOrderEntity = salesOrderService.getById(salesOrder.getId());
		 if(salesOrderEntity==null) {
			 return Result.error("未找到对应数据");
		 }
		 //审核状态为空或审核中则不处理
         if(salesOrder.getFinanceApproveStatus() == null ||
		 salesOrder.getFinanceApproveStatus().equals(ApproveStatusEnum.PENDING.getCode())) {
			 return Result.error("审核状态不能为审核中");
		 }
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 salesOrder.setFinanceApproverId(loginUser.getId());
		 salesOrder.setFinanceApproverName(loginUser.getRealname());
		 salesOrder.setSalesApproveTime(new DateTime());
		 //审核通过时 修改订单状态为待发货
		 if(salesOrder.getFinanceApproveStatus().equals(ApproveStatusEnum.PASS.getCode())) {
			 salesOrder.setOrderStatus(SalesOrderStatusEnum.WAIT_DELIVERY.getCode());
		 }

		 return Result.OK("财务审核成功!");
	 }
	
	/**
	 *  编辑
	 *
	 * @param salesOrderPage
	 * @return
	 */
	@AutoLog(value = "销售订单主表-编辑")
	@Operation(summary="销售订单主表-编辑")
    @RequiresPermissions("scm:mis_sales_order:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SalesOrderPage salesOrderPage) {
		SalesOrder salesOrder = new SalesOrder();
		BeanUtils.copyProperties(salesOrderPage, salesOrder);
		SalesOrder salesOrderEntity = salesOrderService.getById(salesOrder.getId());
		if(salesOrderEntity==null) {
			return Result.error("未找到对应数据");
		}
		salesOrderService.updateMain(salesOrder, salesOrderPage.getSalesOrderDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "销售订单主表-通过id删除")
	@Operation(summary="销售订单主表-通过id删除")
    @RequiresPermissions("scm:mis_sales_order:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		salesOrderService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "销售订单主表-批量删除")
	@Operation(summary="销售订单主表-批量删除")
    @RequiresPermissions("scm:mis_sales_order:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salesOrderService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "销售订单主表-通过id查询")
	@Operation(summary="销售订单主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SalesOrder> queryById(@RequestParam(name="id",required=true) String id) {
		SalesOrder salesOrder = salesOrderService.getById(id);
		if(salesOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(salesOrder);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "销售订单明细表通过主表ID查询")
	@Operation(summary="销售订单明细表主表ID查询")
	@GetMapping(value = "/querySalesOrderDetailByMainId")
	public Result<List<SalesOrderDetail>> querySalesOrderDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<SalesOrderDetail> salesOrderDetailList = salesOrderDetailService.selectByMainId(id);
		return Result.OK(salesOrderDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param salesOrder
    */
    @RequiresPermissions("scm:mis_sales_order:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SalesOrder salesOrder) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<SalesOrder> queryWrapper = QueryGenerator.initQueryWrapper(salesOrder, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<SalesOrder> salesOrderList = salesOrderService.list(queryWrapper);

      // Step.3 组装pageList
      List<SalesOrderPage> pageList = new ArrayList<SalesOrderPage>();
      for (SalesOrder main : salesOrderList) {
          SalesOrderPage vo = new SalesOrderPage();
          BeanUtils.copyProperties(main, vo);
          List<SalesOrderDetail> salesOrderDetailList = salesOrderDetailService.selectByMainId(main.getId());
          vo.setSalesOrderDetailList(salesOrderDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "销售订单主表列表");
      mv.addObject(NormalExcelConstants.CLASS, SalesOrderPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("销售订单主表数据", "导出人:"+sysUser.getRealname(), "销售订单主表"));
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
    @RequiresPermissions("scm:mis_sales_order:importExcel")
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
              List<SalesOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SalesOrderPage.class, params);
              for (SalesOrderPage page : list) {
                  SalesOrder po = new SalesOrder();
                  BeanUtils.copyProperties(page, po);
                  salesOrderService.saveMain(po, page.getSalesOrderDetailList());
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
