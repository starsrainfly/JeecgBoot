package org.jeecg.modules.mes.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.vo.ProductionBatchPage;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.jeecg.modules.mes.service.IProductionBatchBomService;
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
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Tag(name="生产批次")
@RestController
@RequestMapping("/mes/productionBatch")
@Slf4j
public class ProductionBatchController {
	@Autowired
	private IProductionBatchService productionBatchService;
	@Autowired
	private IProductionBatchBomService productionBatchBomService;
	
	/**
	 * 分页列表查询
	 *
	 * @param productionBatch
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产批次-分页列表查询")
	@Operation(summary="生产批次-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionBatch>> queryPageList(ProductionBatch productionBatch,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ProductionBatch> queryWrapper = QueryGenerator.initQueryWrapper(productionBatch, req.getParameterMap());
		Page<ProductionBatch> page = new Page<ProductionBatch>(pageNo, pageSize);
		IPage<ProductionBatch> pageList = productionBatchService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param productionBatchPage
	 * @return
	 */
	@AutoLog(value = "生产批次-添加")
	@Operation(summary="生产批次-添加")
    @RequiresPermissions("mes:mis_production_batch:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionBatchPage productionBatchPage) {
		ProductionBatch productionBatch = new ProductionBatch();
		BeanUtils.copyProperties(productionBatchPage, productionBatch);
		productionBatchService.saveMain(productionBatch, productionBatchPage.getProductionBatchBomList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionBatchPage
	 * @return
	 */
	@AutoLog(value = "生产批次-编辑")
	@Operation(summary="生产批次-编辑")
    @RequiresPermissions("mes:mis_production_batch:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionBatchPage productionBatchPage) {
		ProductionBatch productionBatch = new ProductionBatch();
		BeanUtils.copyProperties(productionBatchPage, productionBatch);
		ProductionBatch productionBatchEntity = productionBatchService.getById(productionBatch.getId());
		if(productionBatchEntity==null) {
			return Result.error("未找到对应数据");
		}
		productionBatchService.updateMain(productionBatch, productionBatchPage.getProductionBatchBomList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产批次-通过id删除")
	@Operation(summary="生产批次-通过id删除")
    @RequiresPermissions("mes:mis_production_batch:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionBatchService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产批次-批量删除")
	@Operation(summary="生产批次-批量删除")
    @RequiresPermissions("mes:mis_production_batch:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionBatchService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产批次-通过id查询")
	@Operation(summary="生产批次-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionBatch> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionBatch productionBatch = productionBatchService.getById(id);
		if(productionBatch==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionBatch);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产批次物料清单通过主表ID查询")
	@Operation(summary="生产批次物料清单主表ID查询")
	@GetMapping(value = "/queryProductionBatchBomByMainId")
	public Result<List<ProductionBatchBom>> queryProductionBatchBomListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProductionBatchBom> productionBatchBomList = productionBatchBomService.selectByMainId(id);
		return Result.OK(productionBatchBomList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionBatch
    */
    @RequiresPermissions("mes:mis_production_batch:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionBatch productionBatch) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ProductionBatch> queryWrapper = QueryGenerator.initQueryWrapper(productionBatch, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ProductionBatch> productionBatchList = productionBatchService.list(queryWrapper);

      // Step.3 组装pageList
      List<ProductionBatchPage> pageList = new ArrayList<ProductionBatchPage>();
      for (ProductionBatch main : productionBatchList) {
          ProductionBatchPage vo = new ProductionBatchPage();
          BeanUtils.copyProperties(main, vo);
          List<ProductionBatchBom> productionBatchBomList = productionBatchBomService.selectByMainId(main.getId());
          vo.setProductionBatchBomList(productionBatchBomList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "生产批次列表");
      mv.addObject(NormalExcelConstants.CLASS, ProductionBatchPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("生产批次数据", "导出人:"+sysUser.getRealname(), "生产批次"));
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
    @RequiresPermissions("mes:mis_production_batch:importExcel")
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
              List<ProductionBatchPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProductionBatchPage.class, params);
              for (ProductionBatchPage page : list) {
                  ProductionBatch po = new ProductionBatch();
                  BeanUtils.copyProperties(page, po);
                  productionBatchService.saveMain(po, page.getProductionBatchBomList());
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
