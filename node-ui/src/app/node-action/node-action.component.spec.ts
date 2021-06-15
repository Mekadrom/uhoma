import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NodeActionComponent } from './node-action.component';

describe('NodeActionComponent', () => {
  let component: NodeActionComponent;
  let fixture: ComponentFixture<NodeActionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NodeActionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NodeActionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
