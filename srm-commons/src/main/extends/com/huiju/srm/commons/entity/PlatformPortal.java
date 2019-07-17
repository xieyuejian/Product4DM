package com.huiju.srm.commons.entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 平台门户配置
 */
@MappedSuperclass
public class PlatformPortal extends BaseEntity<Long> {
private static final long serialVersionUID = 3075133691557657227L;
	/**id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PlatformPortal_PK")
    @TableGenerator(name = "PlatformPortal_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "PlatformPortal_PK",
    allocationSize = 1)
	@Column(name = "platformPortalId")
	protected Long platformPortalId;
	/**是否开放供应商自行注册(1:是;2:否)*/
	@Column(name = "selfRegistration")
	protected String selfRegistration;
	/**物料信息接口同步(1:是;2:否)*/
	@Column(name = "materialSyn")
	protected String materialSyn;
	/**属于哪个管控点(group:集团)*/
	@Column(name = "belong")
	protected String belong;
	/**属于哪个管控点记录id*/
	/*@Column(name = "belongId")
	protected Long belongId;*/
	
	public Long getPlatformPortalId() {
		return platformPortalId;
	}
	public void setPlatformPortalId(Long platformPortalId) {
		this.platformPortalId = platformPortalId;
	}
	public String getSelfRegistration() {
		return selfRegistration;
	}
	public void setSelfRegistration(String selfRegistration) {
		this.selfRegistration = selfRegistration;
	}
	public String getMaterialSyn() {
		return materialSyn;
	}
	public void setMaterialSyn(String materialSyn) {
		this.materialSyn = materialSyn;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	 
	


	
	
	public PlatformPortal() {
	
	}
	public PlatformPortal(Long platformPortalId ) {
		this.platformPortalId = platformPortalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((platformPortalId == null) ? 0 : platformPortalId.hashCode());
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
		PlatformPortal other = (PlatformPortal) obj;
		if (platformPortalId == null) {
			if (other.platformPortalId != null)
				return false;
		} else if (!platformPortalId.equals(other.platformPortalId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdPlatformPortal[platformPortalId=" + platformPortalId + ",selfRegistration=" + selfRegistration + ",materialSyn=" + materialSyn + ",belong=" + belong  + "]";
	}

}
