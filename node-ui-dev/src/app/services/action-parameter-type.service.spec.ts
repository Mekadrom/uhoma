import { TestBed } from '@angular/core/testing';

import { ActionParameterTypeService } from './action-parameter-type.service';

describe('ActionParameterTypeService', () => {
  let service: ActionParameterTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActionParameterTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
