import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewHomeDialogComponent } from './new-home-dialog.component';

describe('NewHomeDialogComponent', () => {
  let component: NewHomeDialogComponent;
  let fixture: ComponentFixture<NewHomeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewHomeDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewHomeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
