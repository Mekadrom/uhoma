import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TypedEditableTableComponent } from './typed-editable-table.component';

describe('TypedEditableTableComponent', () => {
  let component: TypedEditableTableComponent;
  let fixture: ComponentFixture<TypedEditableTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TypedEditableTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TypedEditableTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
