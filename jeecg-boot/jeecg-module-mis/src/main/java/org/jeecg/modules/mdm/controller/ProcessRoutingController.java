package org.jeecg.modules.mdm.controller;

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
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.entity.ProcessRouting;
import org.jeecg.modules.mdm.vo.ProcessRoutingPage;
import org.jeecg.modules.mdm.service.IProcessRoutingService;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
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
 * @Description: 工艺管理
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Tag(name="工艺管理")
@RestController
@RequestMapping("/mdm/processRouting")
@Slf4j
public class ProcessRoutingController {
	@Autowired
	private IProcessRoutingService processRoutingService;
	@Autowired
	private IProcessRoutingStepService processRoutingStepService;
	
	/**
	 * 分页列表查询
	 *
	 * @param processRouting
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "工艺管理-分页列表查询")
	@Operation(summary="工艺管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProcessRouting>> queryPageList(ProcessRouting processRouting,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("isActive", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<ProcessRouting> queryWrapper = QueryGenerator.initQueryWrapper(processRouting, req.getParameterMap(),customeRuleMap);
		Page<ProcessRouting> page = new Page<ProcessRouting>(pageNo, pageSize);
		IPage<ProcessRouting> pageList = processRoutingService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param processRoutingPage
	 * @return
	 */
	@AutoLog(value = "工艺管理-添加")
	@Operation(summary="工艺管理-添加")
    @RequiresPermissions("mdm:mis_process_routing:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProcessRoutingPage processRoutingPage) {
		ProcessRouting processRouting = new ProcessRouting();
		BeanUtils.copyProperties(processRoutingPage, processRouting);
		processRoutingService.saveMain(processRouting, processRoutingPage.getProcessRoutingStepList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param processRoutingPage
	 * @return
	 */
	@AutoLog(value = "工艺管理-编辑")
	@Operation(summary="工艺管理-编辑")
    @RequiresPermissions("mdm:mis_process_routing:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProcessRoutingPage processRoutingPage) {
		ProcessRouting processRouting = new ProcessRouting();
		BeanUtils.copyProperties(processRoutingPage, processRouting);
		ProcessRouting processRoutingEntity = processRoutingService.getById(processRouting.getId());
		if(processRoutingEntity==null) {
			return Result.error("未找到对应数据");
		}
		processRoutingService.updateMain(processRouting, processRoutingPage.getProcessRoutingStepList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工艺管理-通过id删除")
	@Operation(summary="工艺管理-通过id删除")
    @RequiresPermissions("mdm:mis_process_routing:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		processRoutingService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工艺管理-批量删除")
	@Operation(summary="工艺管理-批量删除")
    @RequiresPermissions("mdm:mis_process_routing:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.processRoutingService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工艺管理-通过id查询")
	@Operation(summary="工艺管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProcessRouting> queryById(@RequestParam(name="id",required=true) String id) {
		ProcessRouting processRouting = processRoutingService.getById(id);
		if(processRouting==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(processRouting);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工序步骤通过主表ID查询")
	@Operation(summary="工序步骤主表ID查询")
	@GetMapping(value = "/queryProcessRoutingStepByMainId")
	public Result<List<ProcessRoutingStep>> queryProcessRoutingStepListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProcessRoutingStep> processRoutingStepList = processRoutingStepService.selectByMainId(id);
		return Result.OK(processRoutingStepList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param processRouting
    */
    @RequiresPermissions("mdm:mis_process_routing:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProcessRouting processRouting) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ProcessRouting> queryWrapper = QueryGenerator.initQueryWrapper(processRouting, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ProcessRouting> processRoutingList = processRoutingService.list(queryWrapper);

      // Step.3 组装pageList
      List<ProcessRoutingPage> pageList = new ArrayList<ProcessRoutingPage>();
      for (ProcessRouting main : processRoutingList) {
          ProcessRoutingPage vo = new ProcessRoutingPage();
          BeanUtils.copyProperties(main, vo);
          List<ProcessRoutingStep> processRoutingStepList = processRoutingStepService.selectByMainId(main.getId());
          vo.setProcessRoutingStepList(processRoutingStepList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "工艺管理列表");
      mv.addObject(NormalExcelConstants.CLASS, ProcessRoutingPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("工艺管理数据", "导出人:"+sysUser.getRealname(), "工艺管理"));
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
    @RequiresPermissions("mdm:mis_process_routing:importExcel")
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
              List<ProcessRoutingPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProcessRoutingPage.class, params);
              for (ProcessRoutingPage page : list) {
                  ProcessRouting po = new ProcessRouting();
                  BeanUtils.copyProperties(page, po);
                  processRoutingService.saveMain(po, page.getProcessRoutingStepList());
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
