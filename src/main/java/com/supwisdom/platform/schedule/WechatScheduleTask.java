package com.supwisdom.platform.schedule;

import com.supwisdom.platform.constant.DepartmentLink;
import com.supwisdom.platform.constant.UserLink;
import com.supwisdom.platform.constant.WechatLink;
import com.supwisdom.platform.model.Department;
import com.supwisdom.platform.model.ResponseEntity;
import com.supwisdom.platform.model.Student;
import com.supwisdom.platform.model.Teacher;
import com.supwisdom.platform.service.DepartmentService;
import com.supwisdom.platform.service.StudentService;
import com.supwisdom.platform.service.TeacherService;
import com.supwisdom.platform.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 将sharedb中微信部门同步到企业微信
 */
@Component
@EnableScheduling
public class WechatScheduleTask {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private StudentService studentService;

	/**
	 * 部门全量覆盖，每天凌晨3点执行一次
	 *
	 *  部门必须是数字，不能超过2的32次方
	 * 文件中存在、通讯录中也存在的部门，执行修改操作
	 * 文件中存在、通讯录中不存在的部门，执行添加操作
	 * 文件中不存在、通讯录中存在的部门，当部门下没有任何成员或子部门时，执行删除操作
	 * 文件中不存在、通讯录中存在的部门，当部门下仍有成员或子部门时，暂时不会删除，当下次导入成员把人从部门移出后自动删除
	 * CSV文件中，部门名称、部门ID、父部门ID为必填字段，部门ID必须为数字，根部门的部门id默认为1；排序为可选字段，置空或填0不修改排序, order值大的排序靠前。
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	//@Scheduled(cron = "0 0/1 * * * ?")
	public ResponseEntity syncOrganizeTask() {

		ResponseEntity responseEntity = new ResponseEntity();
		String access_token = "";
		try{
			//1.获取token
			access_token = AuthHelper.getAccessToken();
		}catch (Exception e) {
			e.printStackTrace();
		}
		File file = null;
		try {
			//取得根目录路径
			String rootPath= URLDecoder.decode(getClass().getResource("/").getFile().toString(),"UTF-8");
			String fileName = UUID.randomUUID().toString();
			String filePath = rootPath + "uploads" + File.separator + fileName + ".csv";

			System.out.println(filePath);
			responseEntity.setFilePath(filePath);

			file = new File(filePath);
			boolean res = FileUtils.createFile(filePath);
			if(!res)
				System.out.println("创建失败！");
			else
				System.out.println("创建成功！");

			//获取部门数据列表
			List<String> listString = new ArrayList<>();
			listString.add("部门名称,部门ID,父部门ID");
			List<Department> departments = departmentService.findAll();
			if (null != departments && departments.size() > 0){
				for (Department department : departments) {
					listString.add(department.getName() + "," + department.getId() + "," + department.getPid());
				}
			}

			if(listString.size() >= 1) {
				boolean isSuccess= CSVUtils.exportCsv(file, listString);
				if(isSuccess) {
					JSONObject json = WXUpload.uploadFile(access_token, "file", filePath);
					System.out.println("json===========" + json);
					if(json!=null && StringUtils.isNotBlank(json.getString("media_id"))){
						String media_id = json.getString("media_id");
						System.out.println(media_id);
						String last_wechat_url = WechatLink.API_HOST + DepartmentLink.DEPARTMENT_BATCH_REPLACE_URI + "?access_token=" + access_token; //全量覆盖部门URL
						String jobId = WXUpload.sendCVSData(media_id, last_wechat_url);
						System.out.println("jobId==========" + jobId);

						responseEntity.setJobId(jobId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseEntity;

	}

	/**
	 * 人员增量更新，每4小时执行一次
	 *
	 * 模板中的部门需填写部门ID，多个部门用分号分隔，部门ID必须为数字，根部门的部门id默认为1
	 * 文件中存在、通讯录中也存在的成员，更新成员在文件中指定的字段值
	 * 文件中存在、通讯录中不存在的成员，执行添加操作
	 * 通讯录中存在、文件中不存在的成员，保持不变
	 * 成员字段更新规则：可自行添加扩展字段。文件中有指定的字段，以指定的字段值为准；文件中没指定的字段，不更新
	 */
	@Scheduled(cron = "0 0 */4 * * ?")
	//@Scheduled(cron = "0 0/1 * * * ?")
	public ResponseEntity syncUserTask() {
		ResponseEntity responseEntity = new ResponseEntity();
		String access_token = "";
		try{
			//1.获取token
			access_token = AuthHelper.getAccessToken();
		}catch (Exception e) {
			e.printStackTrace();
		}
		File file = null;
		try {
			//取得根目录路径
			String rootPath= URLDecoder.decode(getClass().getResource("/").getFile().toString(),"UTF-8");
			String fileName = UUID.randomUUID().toString();
			String filePath = rootPath + "uploads" + File.separator + fileName + ".csv";

			System.out.println(filePath);
			responseEntity.setFilePath(filePath);

			file = new File(filePath);
			boolean res = FileUtils.createFile(filePath);
			if(!res)
				System.out.println("创建失败！");
			else
				System.out.println("创建成功！");

			//获取部门数据列表
			List<String> listString = new ArrayList<>();
			listString.add("姓名,帐号,手机号,邮箱,所在部门,性别");
			//查询所有教职工
			List<Teacher> teachers = teacherService.findAll();
			if (null != teachers && teachers.size() > 0){
				for (Teacher teacher : teachers) {
					listString.add(teacher.getXm() + "," + teacher.getGh() + ","
							+ (teacher.getMobile() == null ? "" : teacher.getMobile()) + ","
							+ (teacher.getEmail() == null ? "" : teacher.getEmail()) + ","
							+ teacher.getDepts() + ("2".equals(teacher.getGender()) ? "女" : "男"));
				}
			}
			//查询所有学生
			List<Student> students = studentService.findAll();
			if (null != students && students.size() > 0){
				for (Student student : students) {
					listString.add(student.getXm() + "," + student.getXh() + ","
							+ (student.getMobile() == null ? "" : student.getMobile()) + ","
							+ (student.getEmail() == null ? "" : student.getEmail()) + ","
							+ student.getDepts() + ("2".equals(student.getGender()) ? "女" : "男"));
				}
			}

			if(listString.size() >= 1) {
				boolean isSuccess= CSVUtils.exportCsv(file, listString);
				if(isSuccess) {
					JSONObject json = WXUpload.uploadFile(access_token, "file", filePath);
					//System.out.println("json===========" + json);
					System.out.println(filePath);
					if(json!=null && StringUtils.isNotBlank(json.getString("media_id"))){
						String media_id = json.getString("media_id");
						System.out.println(media_id);
						String last_wechat_url = WechatLink.API_HOST + UserLink.USER_BATCH_UPDATE_URI + "?access_token=" + access_token; //全量覆盖部门URL
						String jobId = WXUpload.sendCVSData(media_id, last_wechat_url);
						System.out.println("jobId==========" + jobId);
						responseEntity.setJobId(jobId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseEntity;
	}

	/**
	 * 级联获取部门下所有人员
	 */
	/*public void getAllUserByDepartmentId() throws Exception{
		String departmentId = "3";
		String get_user_url = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token="+ AuthHelper.getAccessToken() +
				"&department_id=" + departmentId + "&fetch_child=1&status=0";//根据部门获取用户
		JSONObject json_users = WXUpload.getResult(get_user_url); //获取各部门用户

		List<Map<String, Object>> userlist = (List<Map<String, Object>>) json_users.get("userlist");

	}*/

	/**
	 * 批量删除所有用户（一次只能删除200个）
	 */
	/*@Scheduled(cron = "0 0/1 * * * ?")
	public void deleteAllStudent() throws Exception {

		System.out.println("开始-------------");
		//批量删除用户接口
		String batch_del_user_url = UserLink.BATCH_DEL_USER_URL + AuthHelper.getAccessToken();

		List<Student>  userlist = studentService.findAll();		//获取要删除的用户集合
		String userInfos = "";		//存放待删除用户id字符串
		Student user = null;

		if(null != userlist && userlist.size() > 0){
			//遍历删除用户
			for(int i =0; i<userlist.size(); i++){
				user = userlist.get(i);
				//每200个用户删除一次
				if(i%200 == 0){
					//去除最后一个，
					if(userInfos.endsWith(",")){
						userInfos = userInfos.substring(0, userInfos.length()-1);
					}
					//批量删除
					String msg = WXUpload.deleteBatchUser(userInfos, batch_del_user_url);
					if ("error".equals(msg)) {
						System.err.println("---------删除此批用户失败！---------");
						System.err.println(userInfos);
					}
					//置删除字符串为空
					userInfos = "";
				}
				//拼接用户id
				userInfos += "\"" + user.getXh() + "\",";
			}

			//删除最后不足200的人
			if(StringUtils.isNotBlank(userInfos)){
				if(userInfos.endsWith(",")){
					userInfos = userInfos.substring(0, userInfos.length()-1);
				}
				String msg = WXUpload.deleteBatchUser(userInfos, batch_del_user_url);
				if ("error".equals(msg)) {
					System.err.println("---------删除此批用户失败！---------");
					System.err.println(userInfos);
				}
				userInfos = "";
			}
		}
	}*/

	/**
	 * 获取所有部门列表
	 * @throws Exception
	 */
	/*@Scheduled(cron = "0 0/1 * * * ?")
	public void getDeptsByDeptId() throws Exception{
		//获取部门下所有部门数据
		String departmentId = "1";
		String get_dept_url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token="+ AuthHelper.getAccessToken() +
				"&id=" + departmentId;
		JSONObject json_depts = HttpHelper.doGet(get_dept_url);

		if (null != json_depts) {
			List<Map<String, Object>> deptList = (List<Map<String, Object>>)json_depts.get("department");
			//遍历取出所有的部门id列表
			List<String> ids = new ArrayList<>();
			for (Map<String, Object> dept : deptList) {
				ids.add(String.valueOf((Integer)dept.get("id")));
			}

			//获取视图中所有的部门
			List<Department> all = this.departmentService.findAll();
			//获取视图中已经在企业微信的部门
			List<Department> list = all.stream().filter(department -> ids.contains(department.getId())).collect(Collectors.toList());

			System.out.println(list);
		}

	}*/

	public static void main(String[] args) throws Exception {

		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("cpu个数为：" + processors);

		String str = "你好，刘朋龙";
		String[] arr = str.split("，");
		for(int i=0; i<arr.length; i++) {
			System.out.println(arr[i]);
		}

		String accessToken = AuthHelper.getAccessToken();
		System.out.println(accessToken);
	}

}
