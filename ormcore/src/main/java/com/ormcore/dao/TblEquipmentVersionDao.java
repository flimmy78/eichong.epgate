package com.ormcore.dao;

import com.ormcore.model.TblEquipmentVersion;
import com.ormcore.model.TblEquipmentVersionPo;

import java.util.List;

public interface TblEquipmentVersionDao {



   public int insertEqVersion(TblEquipmentVersion equipment);


   public List<TblEquipmentVersion> findEqVersion(TblEquipmentVersion equipment);

   public List<TblEquipmentVersionPo> findEqVersion4Init();

   public List<TblEquipmentVersionPo> findEqVersion4EpInit();

   public int updateEqVersion(TblEquipmentVersion equipment);

   public int deleteEqVersion(int id);

   public int insertEpVersion(TblEquipmentVersion equipment);

   public int updateEpVersion(TblEquipmentVersion equipment);

   public List<TblEquipmentVersion> findEpVersion(int id);

   public int deleteEpVersion(int id);

}
