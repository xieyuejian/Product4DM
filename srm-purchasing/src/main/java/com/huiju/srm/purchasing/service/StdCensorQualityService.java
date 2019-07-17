package com.huiju.srm.purchasing.service;

import java.util.List;

import com.huiju.core.sys.entity.User;
import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.CensorQuality;


/**
 * 质检管理Service接口
 * 
 * @author bairx
 */
public interface StdCensorQualityService extends JpaService<CensorQuality, Long> {

	  /**
     * 同步 <code>CensorQuality</code>进数据库.
     * 
     * @param object
     *            同步CensorQuality对象
     * @return 已经保存进数据库的实例对象
     */
    public CensorQuality mergeCensorQuality(CensorQuality object, Object[] creator);

    /**
     * 同步SAP
     * 
     * @param id
     */
    public String synErp(Long id) throws Exception;

    /**
     * 获取质检单检验人列表
     * 
     * @param clientCode
  * @param object
     * @return
     */
    public List<User> getCensorQualityCheckers(CensorQuality object);

    /**
     * 增加操作日志
     * 
     * @param clientCode
     */
    public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);
}
