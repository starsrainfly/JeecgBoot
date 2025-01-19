package org.jeecg.modules.mis.currency.controller;

import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryRuleEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.HashMap;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mis.currency.entity.ExchangeRate;
import org.jeecg.modules.mis.currency.entity.Currency;
import org.jeecg.modules.mis.currency.service.ICurrencyService;
import org.jeecg.modules.mis.currency.service.IExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 币种表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
@Api(tags="币种表")
@RestController
@RequestMapping("/currency/currency")
@Slf4j
public class CurrencyController extends JeecgController<Currency, ICurrencyService> {

	@Autowired
	private ICurrencyService currencyService;

	@Autowired
	private IExchangeRateService exchangeRateService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param currency
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "币种表-分页列表查询")
	@ApiOperation(value="币种表-分页列表查询", notes="币种表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Currency>> queryPageList(Currency currency,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Currency> queryWrapper = QueryGenerator.initQueryWrapper(currency, req.getParameterMap(),customeRuleMap);
		Page<Currency> page = new Page<Currency>(pageNo, pageSize);
		IPage<Currency> pageList = currencyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
     *   添加
     * @param currency
     * @return
     */
    @AutoLog(value = "币种表-添加")
    @ApiOperation(value="币种表-添加", notes="币种表-添加")
    @RequiresPermissions("currency:mis_currency:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Currency currency) {
        currencyService.save(currency);
        return Result.OK("添加成功！");
    }

    /**
     *  编辑
     * @param currency
     * @return
     */
    @AutoLog(value = "币种表-编辑")
    @ApiOperation(value="币种表-编辑", notes="币种表-编辑")
    @RequiresPermissions("currency:mis_currency:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody Currency currency) {
        currencyService.updateById(currency);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "币种表-通过id删除")
    @ApiOperation(value="币种表-通过id删除", notes="币种表-通过id删除")
    @RequiresPermissions("currency:mis_currency:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        currencyService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "币种表-批量删除")
    @ApiOperation(value="币种表-批量删除", notes="币种表-批量删除")
    @RequiresPermissions("currency:mis_currency:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.currencyService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequiresPermissions("currency:mis_currency:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Currency currency) {
        return super.exportXls(request, currency, Currency.class, "币种表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("currency:mis_currency:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Currency.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-汇率表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "汇率表-通过主表ID查询")
	@ApiOperation(value="汇率表-通过主表ID查询", notes="汇率表-通过主表ID查询")
	@GetMapping(value = "/listExchangeRateByMainId")
    public Result<IPage<ExchangeRate>> listExchangeRateByMainId(ExchangeRate exchangeRate,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<ExchangeRate> queryWrapper = QueryGenerator.initQueryWrapper(exchangeRate, req.getParameterMap());
        Page<ExchangeRate> page = new Page<ExchangeRate>(pageNo, pageSize);
        IPage<ExchangeRate> pageList = exchangeRateService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param exchangeRate
	 * @return
	 */
	@AutoLog(value = "汇率表-添加")
	@ApiOperation(value="汇率表-添加", notes="汇率表-添加")
	@PostMapping(value = "/addExchangeRate")
	public Result<String> addExchangeRate(@RequestBody ExchangeRate exchangeRate) {
		exchangeRateService.save(exchangeRate);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param exchangeRate
	 * @return
	 */
	@AutoLog(value = "汇率表-编辑")
	@ApiOperation(value="汇率表-编辑", notes="汇率表-编辑")
	@RequestMapping(value = "/editExchangeRate", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editExchangeRate(@RequestBody ExchangeRate exchangeRate) {
		exchangeRateService.updateById(exchangeRate);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "汇率表-通过id删除")
	@ApiOperation(value="汇率表-通过id删除", notes="汇率表-通过id删除")
	@DeleteMapping(value = "/deleteExchangeRate")
	public Result<String> deleteExchangeRate(@RequestParam(name="id",required=true) String id) {
		exchangeRateService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "汇率表-批量删除")
	@ApiOperation(value="汇率表-批量删除", notes="汇率表-批量删除")
	@DeleteMapping(value = "/deleteBatchExchangeRate")
	public Result<String> deleteBatchExchangeRate(@RequestParam(name="ids",required=true) String ids) {
	    this.exchangeRateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportExchangeRate")
    public ModelAndView exportExchangeRate(HttpServletRequest request, ExchangeRate exchangeRate) {
		 // Step.1 组装查询条件
		 QueryWrapper<ExchangeRate> queryWrapper = QueryGenerator.initQueryWrapper(exchangeRate, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<ExchangeRate> pageList = exchangeRateService.list(queryWrapper);
		 List<ExchangeRate> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "汇率表");
		 mv.addObject(NormalExcelConstants.CLASS, ExchangeRate.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("汇率表报表", "导出人:" + sysUser.getRealname(), "汇率表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importExchangeRate/{mainId}")
    public Result<?> importExchangeRate(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
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
				 List<ExchangeRate> list = ExcelImportUtil.importExcel(file.getInputStream(), ExchangeRate.class, params);
				 for (ExchangeRate temp : list) {
                    temp.setCurrencyId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 exchangeRateService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-汇率表-end----------------------------------------------*/




}
