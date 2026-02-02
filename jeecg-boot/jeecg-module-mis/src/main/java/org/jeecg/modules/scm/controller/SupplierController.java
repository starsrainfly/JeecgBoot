package org.jeecg.modules.scm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.jeecg.modules.scm.entity.SupplierQualification;
import org.jeecg.modules.scm.entity.SupplierContact;
import org.jeecg.modules.scm.entity.SupplierPurchaser;
import org.jeecg.modules.scm.entity.Supplier;
import org.jeecg.modules.scm.vo.SupplierPage;
import org.jeecg.modules.scm.service.ISupplierService;
import org.jeecg.modules.scm.service.ISupplierQualificationService;
import org.jeecg.modules.scm.service.ISupplierContactService;
import org.jeecg.modules.scm.service.ISupplierPurchaserService;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 供应商表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Tag(name="供应商表")
@RestController
@RequestMapping("/supplier/supplier")
@Slf4j
public class SupplierController {
	@Autowired
	private ISupplierService supplierService;
	@Autowired
	private ISupplierQualificationService supplierQualificationService;
	@Autowired
	private ISupplierContactService supplierContactService;
	@Autowired
	private ISupplierPurchaserService supplierPurchaserService;
	
	/**
	 * 分页列表查询
	 *
	 * @param supplier
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "供应商表-分页列表查询")
	@Operation(summary="供应商表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Supplier>> queryPageList(Supplier supplier,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("supplierType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Supplier> queryWrapper = QueryGenerator.initQueryWrapper(supplier, req.getParameterMap(),customeRuleMap);
		Page<Supplier> page = new Page<Supplier>(pageNo, pageSize);
		IPage<Supplier> pageList = supplierService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param supplierPage
	 * @return
	 */
	@AutoLog(value = "供应商表-添加")
	@Operation(summary="供应商表-添加")
    @RequiresPermissions("supplier:mis_supplier:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SupplierPage supplierPage) {
		Supplier supplier = new Supplier();
		BeanUtils.copyProperties(supplierPage, supplier);
		supplierService.saveMain(supplier, supplierPage.getSupplierQualificationList(),supplierPage.getSupplierContactList(),supplierPage.getSupplierPurchaserList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param supplierPage
	 * @return
	 */
	@AutoLog(value = "供应商表-编辑")
	@Operation(summary="供应商表-编辑")
    @RequiresPermissions("supplier:mis_supplier:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SupplierPage supplierPage) {
		Supplier supplier = new Supplier();
		BeanUtils.copyProperties(supplierPage, supplier);
		Supplier supplierEntity = supplierService.getById(supplier.getId());
		if(supplierEntity==null) {
			return Result.error("未找到对应数据");
		}
		supplierService.updateMain(supplier, supplierPage.getSupplierQualificationList(),supplierPage.getSupplierContactList(),supplierPage.getSupplierPurchaserList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "供应商表-通过id删除")
	@Operation(summary="供应商表-通过id删除")
    @RequiresPermissions("supplier:mis_supplier:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		supplierService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "供应商表-批量删除")
	@Operation(summary="供应商表-批量删除")
    @RequiresPermissions("supplier:mis_supplier:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.supplierService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "供应商表-通过id查询")
	@Operation(summary="供应商表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Supplier> queryById(@RequestParam(name="id",required=true) String id) {
		Supplier supplier = supplierService.getById(id);
		if(supplier==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(supplier);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "供应商质证表通过主表ID查询")
	@Operation(summary="供应商质证表主表ID查询")
	@GetMapping(value = "/querySupplierQualificationByMainId")
	public Result<List<SupplierQualification>> querySupplierQualificationListByMainId(@RequestParam(name="id",required=true) String id) {
		List<SupplierQualification> supplierQualificationList = supplierQualificationService.selectByMainId(id);
		return Result.OK(supplierQualificationList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "供应商联系人通过主表ID查询")
	@Operation(summary="供应商联系人主表ID查询")
	@GetMapping(value = "/querySupplierContactByMainId")
	public Result<List<SupplierContact>> querySupplierContactListByMainId(@RequestParam(name="id",required=true) String id) {
		List<SupplierContact> supplierContactList = supplierContactService.selectByMainId(id);
		return Result.OK(supplierContactList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "供应商采购员通过主表ID查询")
	@Operation(summary="供应商采购员主表ID查询")
	@GetMapping(value = "/querySupplierPurchaserByMainId")
	public Result<List<SupplierPurchaser>> querySupplierPurchaserListByMainId(@RequestParam(name="id",required=true) String id) {
		List<SupplierPurchaser> supplierPurchaserList = supplierPurchaserService.selectByMainId(id);
		return Result.OK(supplierPurchaserList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param supplier
    */
    @RequiresPermissions("supplier:mis_supplier:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Supplier supplier) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<Supplier> queryWrapper = QueryGenerator.initQueryWrapper(supplier, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<Supplier> supplierList = supplierService.list(queryWrapper);

      // Step.3 组装pageList
      List<SupplierPage> pageList = new ArrayList<SupplierPage>();
      for (Supplier main : supplierList) {
          SupplierPage vo = new SupplierPage();
          BeanUtils.copyProperties(main, vo);
          List<SupplierQualification> supplierQualificationList = supplierQualificationService.selectByMainId(main.getId());
          vo.setSupplierQualificationList(supplierQualificationList);
          List<SupplierContact> supplierContactList = supplierContactService.selectByMainId(main.getId());
          vo.setSupplierContactList(supplierContactList);
          List<SupplierPurchaser> supplierPurchaserList = supplierPurchaserService.selectByMainId(main.getId());
          vo.setSupplierPurchaserList(supplierPurchaserList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "供应商表列表");
      mv.addObject(NormalExcelConstants.CLASS, SupplierPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("供应商表数据", "导出人:"+sysUser.getRealname(), "供应商表"));
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
    @RequiresPermissions("supplier:mis_supplier:importExcel")
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
              List<SupplierPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SupplierPage.class, params);
              for (SupplierPage page : list) {
                  Supplier po = new Supplier();
                  BeanUtils.copyProperties(page, po);
                  supplierService.saveMain(po, page.getSupplierQualificationList(),page.getSupplierContactList(),page.getSupplierPurchaserList());
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
