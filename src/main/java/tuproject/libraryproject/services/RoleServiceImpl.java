package tuproject.libraryproject.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuproject.libraryproject.domain.entities.UserRole;
import tuproject.libraryproject.domain.models.view.UserRoleViewModel;
import tuproject.libraryproject.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UserRoleViewModel> extractAllRoles() {
        List<UserRole> all = this.roleRepository.findAll();
        List<UserRoleViewModel> userRoleViewModels = new ArrayList<>();

        for (UserRole userRole : all) {
            if (userRole.getAuthority().equals("ROOT")) {
                continue;
            }
            UserRoleViewModel userRoleViewModel = this.modelMapper.map(userRole, UserRoleViewModel.class);
            userRoleViewModels.add(userRoleViewModel);
        }
        return userRoleViewModels;
    }
}
