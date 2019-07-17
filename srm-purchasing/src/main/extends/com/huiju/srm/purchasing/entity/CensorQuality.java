package com.huiju.srm.purchasing.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <pre>质检管理表 基准类</pre>
 * @author bairx
 * @version 1.0 时间 2019/3/30 
 */

@Entity
@Table(name="d_cp_censorquality")
public class CensorQuality extends StdCensorQuality{

	private static final long serialVersionUID = 3075133691557657227L;
	
}
