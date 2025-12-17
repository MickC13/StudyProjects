package Districts;

import java.util.List;

public interface DistrictDataManager {
    List<District> getDistricts();
    void addDistrict(District district);
    void updateDistrict(District district);
    void deleteDistrict(int id);
    District getDistrictById(int id);
    District getDistrictByName(String name);
    List<District> getDistrictsByPostmanCount(int minPostmen);
}