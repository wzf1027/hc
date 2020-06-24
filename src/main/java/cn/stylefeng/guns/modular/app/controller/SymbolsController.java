package cn.stylefeng.guns.modular.app.controller;


import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.system.service.SymbolsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;


/**
 * 币种控制器
 */
@RestController
@RequestMapping("/app/symbols")
public class SymbolsController {

	@Resource
	private SymbolsService symbolsService;


	/**
	 * 获取symbolList的聚合行情ticker信息同时提供最近24小时的交易信息
	 * @return ResponseData
	 */
	@RequestMapping(value="/homeSymbols")
	public ResponseData homeSymbols() {
		return symbolsService.selectSymbolsList();
	}




}
