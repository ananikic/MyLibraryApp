package tuproject.libraryproject.services;

import tuproject.libraryproject.domain.models.view.UserRoleViewModel;

import java.util.List;

public interface RoleService {
    List<UserRoleViewModel> extractAllRoles();
}
