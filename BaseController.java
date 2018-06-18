package com.cloud.base;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.cloud.common.GlobalCode;
import com.cloud.common.object.HMap;
import com.cloud.common.utils.CommonUtils;

public abstract class BaseController {

	/**
	 * 输出json
	 * @param response
	 * @param html
	 * @return
	 * @author 
	 */
	protected String json(HttpServletResponse response, JSONObject json) {
		try {
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return null;
	}

	protected String data(HttpServletResponse response, String code, String msg) {
		try {
			JSONObject json = new JSONObject();
			
			json.put("code", code);
			json.put("msg", msg);
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	protected String data(HttpServletResponse response, String code, JSONObject bodyjson, String msg) {
		try {
			JSONObject json = new JSONObject();
			json.put("body", bodyjson);
			json.put("code", code);
			json.put("msg", msg);
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * 返回Json : success
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String success(HttpServletResponse response) {
		JSONObject json = new JSONObject();
		json.put("code", GlobalCode.INTERFACE_CODE_SUCESS);
		json.put("msg", GlobalCode.INTERFACE_MESSAGE_SUCESS);
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	/**
	 * 返回Json : success
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String success(HttpServletResponse response, JSONObject bodyjson) {
		try {
			JSONObject json = new JSONObject();
			json.put("code", GlobalCode.INTERFACE_CODE_SUCESS);
			json.put("msg", GlobalCode.INTERFACE_MESSAGE_SUCESS);
			json.put("body", bodyjson);
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	/**
	 * 转到一个页面
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	protected String page(HttpServletRequest request, String page) {
		return page;
	}

	/**
	 * 返回Json : error
	 * 
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String error(HttpServletResponse response) {
		JSONObject json = new JSONObject();
		json.put("code", GlobalCode.INTERFACE_CODE_ERROR);
		json.put("msg", GlobalCode.INTERFACE_MESSAGE_ERROR);
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 返回Json : error
	 * 
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String error(HttpServletResponse response, String msg) {
		JSONObject json = new JSONObject();
		json.put("code", GlobalCode.INTERFACE_CODE_ERROR);
		json.put("msg", msg);
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 返回Json : error
	 * 
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String error(HttpServletResponse response, String code, String msg) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("msg", msg);

		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 返回Json : error
	 * 
	 * @param response
	 * @param msg
	 * @return
	 * @author 
	 */
	protected String error(HttpServletResponse response, String code, String msg, JSONObject bodyjson) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("msg", msg);
		json.put("bodyjson", bodyjson);
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 获取request请求的参数
	 * @param request
	 * @return
	 */
	protected HMap getParameterMap(HttpServletRequest request) {
		HMap paramMap = new HMap();

		Enumeration<String> enums = request.getParameterNames();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			String value = CommonUtils.parameterDecode(request, key);
			paramMap.put(key, value);
		}

		return paramMap;
	}

}
