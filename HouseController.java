package com.cloud.appcan.house.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.common.GlobalCode;
import com.cloud.common.GlobalConstrant;
import com.cloud.common.object.HMap;
import com.cloud.common.utils.CommonUtils;
import com.cloud.house.service.HouseServices;
import com.cloud.room.service.RoomServices;
import com.cloud.usermanage.sys.services.MediaManageService;
import com.cloud.usermanage.utils.LandlordManageUtils;
import com.cloud.usermanage.utils.SystemManageUtils;

/**
 * @Description: APPCAN房屋管理 
 * @author tfwang 
 * @date 2017年5月6日 下午2:27:27 
 */
@Controller
@RequestMapping(value = "/appcan/house")
public class HouseController extends com.cloud.base.BaseController {
	@Autowired
	private HouseServices houseServices;
	@Autowired
	private RoomServices roomServices;
	@Autowired
	private MediaManageService mediaManageService;

	/**
	 * 获取房产信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/houseList")
	@ResponseBody
	public String getHouseList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {

			String userid = LandlordManageUtils.getCurrentUserId();
			if (StringUtils.isBlank(userid)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			List<HMap> houseList = houseServices.getHouseList(userid);
			json.put("houseList", houseList);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, GlobalCode.INTERFACE_MESSAGE_SUCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}

	}

	/**  
	 * 删除房产  房产必须没有在租的房客  账单必须结清
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/delHouse")
	@ResponseBody
	public String delHouse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseId = CommonUtils.parameterDecode(request, "houseId");
			if (StringUtils.isBlank(houseId)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			// 判断房产内房屋是否正在出租
			int rentCount = houseServices.getRentCount(houseId);
			if (rentCount > 0) {
				json.put("rentCount", rentCount);
				return data(response, "N", json, null);
			}
			int res = houseServices.delHouse(houseId);
			if (res == 1) {
				return success(response);
			} else {
				return error(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 获取出租屋详情
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getHouseDetails")
	@ResponseBody
	public String getHouseDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			String houseid = request.getParameter("houseid");
			if (StringUtils.isBlank(houseid)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}

			// 获取出租屋信息
			HMap houseMap = houseServices.getHouseInfo(houseid);
			// 获取出租屋的房间信息
			List<HMap> roomList = roomServices.getRoomByHouseId(houseid);
			// 获取是否在租标识
			List<HMap> floorList = houseServices.getFloorByHouseId(houseid);

			JSONObject json = new JSONObject();
			// 还未获得托管标识
			json.put("houseMap", houseMap);
			json.put("roomList", roomList);
			json.put("floorList", floorList);

			return success(response, json);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 获取出租屋配置数据页面
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getHouseSetingPage")
	@ResponseBody
	public String getHouseSetingPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseid = request.getParameter("houseid");
			if (StringUtils.isBlank(houseid)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}

			// 获取出租屋的房间信息
			List<HMap> roomList = roomServices.getRoomByRoomIdForRoomConfig(houseid);
			List<HMap> roomTypeList = roomServices.getAllRoomTypeList();

			json.put("roomList", roomList);
			json.put("roomTypeList", roomTypeList);

			return success(response, json);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 修改出租屋
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateHouse")
	@ResponseBody
	public String updateHouse(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			String houseId = CommonUtils.parameterDecode(request, "houseId");
			String userId = LandlordManageUtils.getCurrentUserId();
			String name = CommonUtils.parameterDecode(request, "houseName");
			String districtId = CommonUtils.parameterDecode(request, "districtId");
			String streetId = CommonUtils.parameterDecode(request, "streetId");
			String detailaddree = CommonUtils.parameterDecode(request, "detailaddree");

			String defaultwaterprice = CommonUtils.parameterDecode(request, "defaultwaterprice");
			String defaultelecprice = CommonUtils.parameterDecode(request, "defaultelecprice");
			String defaultphone = CommonUtils.parameterDecode(request, "defaultphone");
			String defaultnickname = CommonUtils.parameterDecode(request, "defaultnickname");
			String enterbillday = CommonUtils.parameterDecode(request, "enterbillday");

			if (StringUtils.isBlank(userId) || StringUtils.isBlank(houseId) || StringUtils.isBlank(name) || StringUtils.isBlank(districtId) || StringUtils.isBlank(streetId) || StringUtils.isBlank(detailaddree) || StringUtils.isBlank(defaultwaterprice) || StringUtils.isBlank(defaultelecprice) || StringUtils.isBlank(defaultphone) || StringUtils.isBlank(defaultnickname) || StringUtils.isBlank(enterbillday)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap houseMap = new HMap();
			houseMap.put("houseId", houseId);
			houseMap.put("name", name);
			houseMap.put("districtId", districtId);
			houseMap.put("streetId", streetId);
			houseMap.put("detailaddree", detailaddree);

			houseMap.put("defaultwaterprice", defaultwaterprice);
			houseMap.put("defaultelecprice", defaultelecprice);
			houseMap.put("defaultphone", defaultphone);
			houseMap.put("defaultnickname", defaultnickname);
			houseMap.put("enterbillday", enterbillday);
			houseMap.put("houseid", houseId);
			int res = houseServices.updateHouse(houseMap);
			if (res > 0)
				return success(response);
			else
				return error(response);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}

	}

	/**
	 * 获取修改页面信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateHousePage")
	@ResponseBody
	public String updateHousePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseId = CommonUtils.parameterDecode(request, "houseId");
			String userId = LandlordManageUtils.getCurrentUserId();
			if (StringUtils.isBlank(userId)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap houseMap = houseServices.getHouseInfo(houseId);
			HMap paramMap = new HMap();
			paramMap.put("targetid", houseId);
			paramMap.put("mediatype", GlobalConstrant.MEDIA_TYPE_1);
			paramMap.put("mediatarget", GlobalConstrant.MEDIA_TARGET_HOUSE);
			// 获取照片信息
			List<HMap> picList = mediaManageService.getMediaList(paramMap);
			json.put("houseMap", houseMap);
			json.put("picList", picList);
			return success(response, json);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}

	}

	/**
	 * 新增房屋
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/addHouse")
	@ResponseBody
	public String addHouse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {

			// 获取数据
			String houseId = CommonUtils.parameterDecode(request, "houseId");
			String userId = LandlordManageUtils.getCurrentUserId();

			String name = CommonUtils.parameterDecode(request, "houseName");
			String roomNumbers = CommonUtils.parameterDecode(request, "roomNumbers");
			String districtId = CommonUtils.parameterDecode(request, "districtId");
			String streetId = CommonUtils.parameterDecode(request, "streetId");
			String detailAddree = CommonUtils.parameterDecode(request, "detailArea");
			String floorTotal = CommonUtils.parameterDecode(request, "floorTotal");
			String roomTotal = CommonUtils.parameterDecode(request, "roomTotal");

			String defaultwaterprice = CommonUtils.parameterDecode(request, "defaultwaterprice");
			String defaultelecprice = CommonUtils.parameterDecode(request, "defaultelecprice");
			String defaultphone = CommonUtils.parameterDecode(request, "defaultphone");
			String defaultnickname = CommonUtils.parameterDecode(request, "defaultnickname");
			String enterbillday = CommonUtils.parameterDecode(request, "enterbillday");

			// 判断参数是否为空
			if (StringUtils.isBlank(name) || StringUtils.isBlank(districtId) || StringUtils.isBlank(streetId) || StringUtils.isBlank(detailAddree) || StringUtils.isBlank(floorTotal) || StringUtils.isBlank(roomTotal) || StringUtils.isBlank(detailAddree) || StringUtils.isBlank(defaultwaterprice) || StringUtils.isBlank(defaultelecprice) || StringUtils.isBlank(defaultphone) || StringUtils.isBlank(defaultnickname) || StringUtils.isBlank(enterbillday)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			// 参数置入Map
			HMap houseMap = new HMap();
			houseMap.put("houseId", houseId);
			houseMap.put("userId", userId);
			houseMap.put("name", name);
			houseMap.put("districtId", districtId);
			houseMap.put("streetId", streetId);
			houseMap.put("detailAddree", detailAddree);

			houseMap.put("defaultwaterprice", defaultwaterprice);
			houseMap.put("defaultelecprice", defaultelecprice);
			houseMap.put("defaultphone", defaultphone);
			houseMap.put("defaultnickname", defaultnickname);
			houseMap.put("enterbillday", enterbillday);
			houseMap.put("houseid", houseId);
			houseMap.put("roomNumbers", roomNumbers);

			houseMap.put("floorTotal", floorTotal);
			houseMap.put("roomTotal", roomTotal);

			int res = houseServices.addHouse(houseMap);
			if (res > 0)
				return success(response);
			else
				return error(response, "");
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}

	}

	/**
	 * 获取深圳所有区
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getAllDistrictList")
	@ResponseBody
	public String getAllDistrictList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			List<HMap> disList = houseServices.getAllDistrictList();
			json.put("disList", disList);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, null);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 获取深圳区所有街道
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getStreetList")
	@ResponseBody
	public String getStreetList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String districtId = request.getParameter("districtId");
			if (StringUtils.isBlank(districtId)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			List<HMap> streetList = houseServices.getStreetList(districtId);
			json.put("streetList", streetList);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, null);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 获取简单的出租屋信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getSimpleHouseInfo")
	@ResponseBody
	public String getSimpleHouseInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseId = request.getParameter("houseId");
			if (StringUtils.isBlank(houseId)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap houseMap = houseServices.getHouseInfo(houseId);
			json.put("houseMap", houseMap);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, null);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 获取出租屋户型对应的房间设置信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getHouseRoomTypeSeting")
	@ResponseBody
	public String getHouseRoomTypeSeting(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseid = request.getParameter("houseid");
			String roomType = request.getParameter("roomType");
			if (StringUtils.isBlank(houseid) || StringUtils.isBlank(roomType)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap hMap = new HMap();
			hMap.put("houseid", houseid);
			hMap.put("roomType", roomType);
			List<HMap> houseConfig = houseServices.getHouseRoomTypeSeting(hMap);
			// 获取房间所有布局类型 例如（主卧，次卧）
			List<HMap> housePartList = SystemManageUtils.getDictionaryListByType(GlobalConstrant.DIC_ATTRTYPE_ROOM_TYPE_PART);

			json.put("houseConfig", houseConfig);
			json.put("housePartList", housePartList);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, null);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 修改系统出租屋房间配置信息
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/updateHouseConfig")
	@ResponseBody
	public String updateHouseConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String houseid = CommonUtils.parameterDecode(request, "houseid");
			String goodsids = CommonUtils.parameterDecode(request, "goodsids");
			String roomType = CommonUtils.parameterDecode(request, "roomType");
			String area = CommonUtils.parameterDecode(request, "area");
			String deliveryway = CommonUtils.parameterDecode(request, "deliveryway");
			String detail = CommonUtils.parameterDecode(request, "detail");

			String[] goodsId = StringUtils.isBlank(goodsids) ? null : goodsids.split(",");

			if (StringUtils.isBlank(houseid) || StringUtils.isBlank(goodsids) || StringUtils.isBlank(roomType) || StringUtils.isBlank(area) || StringUtils.isBlank(deliveryway)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap roomMap = new HMap();
			roomMap.put("houseid", houseid);
			roomMap.put("goodsId", goodsId);
			roomMap.put("roomType", roomType);
			roomMap.put("area", area);
			roomMap.put("deliveryway", deliveryway);
			roomMap.put("detail", detail);

			int res = houseServices.updateHouseConfig(roomMap);
			if (res > 0)
				return success(response);
			else
				return error(response);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 选择房间类型页面
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getChooseRoomSettingPage")
	@ResponseBody
	public String getChooseRoomSettingPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		try {
			String houseid = CommonUtils.parameterDecode(request, "houseid");

			if (StringUtils.isBlank(houseid)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}

			// 获取出租屋的房间信息
			List<HMap> roomList = roomServices.getRoomByRoomIdForRoomConfig(houseid);

			json.put("roomList", roomList);
			return data(response, GlobalCode.INTERFACE_CODE_SUCESS, json, null);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}

	/**
	 * 修改房间类型并根据对应的出租屋房间系统配置修改房间配置 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/updateRoomTypeAndUpdateRoomConfig")
	@ResponseBody
	public String updateRoomTypeAndUpdateRoomConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String houseid = CommonUtils.parameterDecode(request, "houseid");
			String roomIds = CommonUtils.parameterDecode(request, "roomIds");
			String roomType = CommonUtils.parameterDecode(request, "roomType");
			String[] roomId = StringUtils.isBlank(roomIds) ? null : roomIds.split(",");

			if (StringUtils.isBlank(houseid) || StringUtils.isBlank(roomType) || StringUtils.isBlank(roomIds)) {
				return error(response, GlobalCode.INTERFACE_CODE_PARAM_ERROR, GlobalCode.INTERFACE_MESSAGE_PARAM_ERROR);
			}
			HMap roomMap = new HMap();
			roomMap.put("houseid", houseid);
			roomMap.put("roomId", roomId);
			roomMap.put("roomType", roomType);

			int res = roomServices.updateRoomTypeAndUpdateRoomConfig(roomMap);
			if (res > 0)
				return success(response);
			else
				return error(response);
		} catch (Exception e) {
			e.printStackTrace();
			return error(response);
		}
	}
}
