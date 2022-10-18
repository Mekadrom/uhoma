import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardMenubarButtonsComponent } from './dashboard-menubar-buttons.component';

describe('DashboardMenubarButtonsComponent', () => {
  let component: DashboardMenubarButtonsComponent;
  let fixture: ComponentFixture<DashboardMenubarButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardMenubarButtonsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardMenubarButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
