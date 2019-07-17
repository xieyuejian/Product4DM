package com.huiju.srm.commons.entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 库存管理
 */
@MappedSuperclass
public class InventoryManagement extends BaseEntity<Long> {
    private static final long serialVersionUID = 3075133691557657227L;
	/**id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "InventoryManagement_PK")
    @TableGenerator(name = "InventoryManagement_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "InventoryManagement_PK",
    allocationSize = 1)
	@Column(name = "inventoryManagementId")
	protected Long inventoryManagementId;
	/**库存内部接口是否启用*/
	@Column(name = "inventoryEnabled")
	protected String inventoryEnabled;
	/**所属管控点*/
	@Column(name = "belong")
	protected String belong;
	/**所属管控点id*/
	/*@Column(name = "belongId")
	protected Long belongId;*/
	
	public Long getInventoryManagementId() {
		return inventoryManagementId;
	}
	public void setInventoryManagementId(Long inventoryManagementId) {
		this.inventoryManagementId = inventoryManagementId;
	}
	public String getInventoryEnabled() {
		return inventoryEnabled;
	}
	public void setInventoryEnabled(String inventoryEnabled) {
		this.inventoryEnabled = inventoryEnabled;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	 
	


	
	
	public InventoryManagement() {
	
	}
	public InventoryManagement(Long inventoryManagementId ) {
		this.inventoryManagementId = inventoryManagementId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inventoryManagementId == null) ? 0 : inventoryManagementId.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryManagement other = (InventoryManagement) obj;
		if (inventoryManagementId == null) {
			if (other.inventoryManagementId != null)
				return false;
		} else if (!inventoryManagementId.equals(other.inventoryManagementId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdInventoryManagement[inventoryManagementId=" + inventoryManagementId + ",inventoryEnabled=" + inventoryEnabled + ",belong=" + belong  + "]";
	}

}
