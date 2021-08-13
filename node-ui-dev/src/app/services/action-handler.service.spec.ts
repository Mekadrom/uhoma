import { TestBed } from '@angular/core/testing';

import { ActionHandlerService } from './action-handler.service';

describe('ActionHandlerService', () => {
  let service: ActionHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActionHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
