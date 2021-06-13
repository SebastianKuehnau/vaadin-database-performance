package com.example.views.employee;

import com.example.model.employee.control.EmployeeRepository;
import com.example.model.employee.entity.Employee;
import com.example.views.main.ApplicationLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Stream;

@Route(value = "employee-tree", layout = ApplicationLayout.class)
@PageTitle("Employee Tree")
public class EmployeeTreeView extends Div {

    public EmployeeTreeView(EmployeeRepository employeeRepository) {
        setHeightFull();

        var treeGrid = new TreeGrid<Employee>();
        treeGrid.addHierarchyColumn(employee -> employee.getFirstName() + " " + employee.getLastName()).setAutoWidth(true);
        treeGrid.addColumn(Employee::getEmail).setAutoWidth(true);

        var dataProvider =
                new AbstractBackEndHierarchicalDataProvider<Employee, Void>() {

                    @Override
                    public int getChildCount(HierarchicalQuery<Employee, Void> query) {
                        if (query.getParent() == null) {
                            return employeeRepository.countBySupervisorIsNull();
                        } else {
                            return employeeRepository.countBySupervisor(query.getParent());
                        }
                    }

                    @Override
                    public boolean hasChildren(Employee employee) {
                        return employeeRepository.countBySupervisor(employee) > 0;
                    }

                    @Override
                    protected Stream<Employee> fetchChildrenFromBackEnd(HierarchicalQuery<Employee, Void> query) {
                        if (query.getParent() == null) {
                            return employeeRepository.findAllBySupervisorIsNull().stream();
                        } else {
                            return employeeRepository.findAllBySupervisor(query.getParent()).stream();
                        }
                    }
                };

        treeGrid.setDataProvider(dataProvider);

        treeGrid.setHeightFull();

        add(treeGrid);
    }
}
