package com.test.spring.cloud.components.datatables;

import com.test.spring.cloud.common.dto.BaseResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Bootstrap Datatables 结果集     注意:继承了BaseResult! 将需要的属性集进行了封装(无论从哪里请求结果都可以拿到完整,并附带额外需要的结果)
 * <p>Title: DatatablesResult</p>
 * <p>Description: </p>
 *
 * @author Lusifer
 * @version 1.0.0
 * @date 2018/8/12 13:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataTablesResult extends BaseResult implements Serializable {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private String error;
}
