import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardBreadcrumbsComponent } from './dashboard-breadcrumbs.component';

describe('DashboardBreadcrumbsComponent', () => {
  let component: DashboardBreadcrumbsComponent;
  let fixture: ComponentFixture<DashboardBreadcrumbsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardBreadcrumbsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardBreadcrumbsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
