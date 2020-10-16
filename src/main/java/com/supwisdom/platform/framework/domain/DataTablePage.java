package com.supwisdom.platform.framework.domain;


import java.util.List;
import java.util.Map;

/**
 * dataTable分页插件需要的信息
 * 
 * @author yao.chen
 *
 */
public class DataTablePage extends FrameworkPage {

    //DataTables 用来生成的信息
    public String sEcho;

    //显示的起始索引
    public int iDisplayStart;

    //显示的行数
    public int iDisplayLength;

    //查询结果的条数
    public int iTotalDisplayRecords;

    //总条数
    public int iTotalRecords;

    //是否分页，默认不分页
    public boolean pageState = false;

    //页面查询参数
    private Map<String, Object> mapBean;

    //查询结果
    protected Object aaData = null;

    public String getsEcho() {
        return sEcho;
    }

    public void setsEcho(String sEcho) {
        this.sEcho = sEcho;
    }

    public int getiDisplayStart() {
        return iDisplayStart;
    }

    public void setiDisplayStart(int iDisplayStart) {
        this.iDisplayStart = iDisplayStart;
    }

    public int getiDisplayLength() {
        return iDisplayLength;
    }

    public void setiDisplayLength(int iDisplayLength) {
        this.iDisplayLength = iDisplayLength;
    }

    public int getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public int getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(int iTotalRecords) {
        super.setTotalItems(iTotalRecords);
        this.iTotalRecords = iTotalRecords;
    }

    public Map<String, Object> getMapBean() {
        return mapBean;
    }

    public void setMapBean(Map<String, Object> mapBean) {
        this.mapBean = mapBean;
    }

    public Object getAaData() {
        return aaData;
    }

    //查询结果
    @SuppressWarnings("rawtypes")
    public void setAaData(Object aaData) {
        //this.aaData = aaData;
        super.setItems(aaData);
        if (!super.isLoadAll()) {
            if (this.iDisplayLength != 0)
                super.setTotalPages((this.iTotalRecords - 1) / this.iDisplayLength + 1);
            if (aaData instanceof List)
                super.setCurrentItemCount(((List) aaData).size());
        } else {
            if (aaData instanceof List) {
                super.setCurrentItemCount(((List) aaData).size());
                super.setTotalItems(((List) aaData).size());
            }
        }
    }

    public boolean getPageState() {
        return pageState;
    }

    public void setPageState(boolean pageState) {
        this.pageState = pageState;
    }

    /**
     * 将BizPage的参数赋给DataTablePage
     */
    public void internalSet() {
        if (!isLoadAll()) {
            this.pageState = true;
            this.iDisplayStart = super.getItemsPerPage() * super.getPageIndex();
            this.iDisplayLength = super.getItemsPerPage();
        } else {
            if (super.getTotalItems() > 0) {
                this.pageState = true;
                this.iDisplayStart = 0;
                this.iDisplayLength = super.getTotalItems();
            } else {
                this.pageState = false;
                this.iDisplayStart = 0;
            }
        }
        super.setStartIndex(this.iDisplayStart);

    }

}
