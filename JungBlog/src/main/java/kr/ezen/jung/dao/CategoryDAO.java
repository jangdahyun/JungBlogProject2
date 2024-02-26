package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDAO {
	List<String> selectCategory() throws SQLException;
	String selectCategoryBycategoryNum(int categoryNum) throws SQLException;
}
