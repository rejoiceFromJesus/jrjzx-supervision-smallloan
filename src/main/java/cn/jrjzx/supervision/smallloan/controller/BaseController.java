/**
 * 系统项目名称
 * cn.jrjzx.supervision.smallloan.controller
 * BaseController.java
 * 
 * 2017年6月9日-下午2:28:19
 *  2017金融街在线公司-版权所有
 *
 */
package cn.jrjzx.supervision.smallloan.controller;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.jrjzx.supervision.smallloan.common.bean.EasyUIResult;
import cn.jrjzx.supervision.smallloan.common.exception.ResourceNotFoundException;
import cn.jrjzx.supervision.smallloan.service.BaseService;

import com.github.pagehelper.PageInfo;

/**
 *
 * BaseController
 * 
 * @author rejoice 948870341@qq.com
 * @date 2017年6月9日 下午2:28:19
 * 
 * @version 1.0.0
 *
 */ 
public class BaseController<T,S> { 

	
	@Autowired
	BaseService<T> baseService;
	
	@SuppressWarnings("unused")
	private Class<T> clazz;
	
	
	@SuppressWarnings("unchecked")
	protected S getService(){
		return (S) baseService;
	}
	
  @SuppressWarnings("unchecked")
    public BaseController() {
        super();
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType ptype = (ParameterizedType) type;
        this.clazz = (Class<T>) ptype.getActualTypeArguments()[0];
    }

	
	@GetMapping("all")
	public ResponseEntity<List<T>> findAll(T t){
		return ResponseEntity.ok(baseService.queryListByWhere(t));
	}
	@GetMapping("/page") 
	public ResponseEntity<EasyUIResult> findByPage(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
			@RequestParam(value = "limit", defaultValue = "30") Integer rows,String[] sort,String[] order, T t) throws Exception{
		Field flag = ReflectionUtils.findField(t.getClass(), "flag");
    	flag.setAccessible(true);
    	flag.set(t,1);
		PageInfo<T> pageInfo = baseService.queryListByPageAndOrder(t, (offset/rows+1), rows,sort,order);
		 EasyUIResult easyUIResult = new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
		return ResponseEntity.ok(easyUIResult);
	}
	@PutMapping
	public void update(@RequestBody T t) throws Exception{
		baseService.updateByIdSelective(t);
	}     
	
	@PostMapping 
	public void save(@RequestBody T t) throws Exception{
		baseService.saveSelective(t);
	}
	@DeleteMapping("{ids}")
	public void delete(@PathVariable("ids") String ids) throws Exception{
		List<String>  idsArray = Arrays.asList(ids.split(","));
		T t = null;
		for(String id: idsArray){
			t = clazz.newInstance();
			Field flag = ReflectionUtils.findField(t.getClass(), "flag");
			flag.setAccessible(true);
			flag.set(t, 0);
	    	Field fieldId = ReflectionUtils.findField(t.getClass(), "id");
	    	fieldId.setAccessible(true);
	    	Class<?> type = fieldId.getType();
	    	if(type.equals(Integer.class)||type.toString().equals("int")){
	    		fieldId.set(t, Integer.parseInt(id));
	    	}else if(type.equals(Long.class)||type.toString().equals("long")){
	    		fieldId.set(t, Long.parseLong(id));
	    	}else if(type.equals(String.class)){
	    		fieldId.set(t, id);
	    	}else{
	    		throw new RuntimeException("unsupported id type:"+type);
	    	}
			baseService.updateByIdSelective(t);
		}
	}
	@GetMapping("{id}")
	public T getById(@PathVariable("id") Serializable id){
		T t = baseService.queryByID(id);
		if(t == null){
			throw new ResourceNotFoundException();
		}
		return t;
	} 
}
