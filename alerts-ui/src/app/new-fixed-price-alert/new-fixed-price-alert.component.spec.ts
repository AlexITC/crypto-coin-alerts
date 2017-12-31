import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewFixedPriceAlertComponent } from './new-fixed-price-alert.component';

describe('NewFixedPriceAlertComponent', () => {
  let component: NewFixedPriceAlertComponent;
  let fixture: ComponentFixture<NewFixedPriceAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewFixedPriceAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewFixedPriceAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
