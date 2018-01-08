import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FixedPriceAlertsComponent } from './fixed-price-alerts.component';

describe('FixedPriceAlertsComponent', () => {
  let component: FixedPriceAlertsComponent;
  let fixture: ComponentFixture<FixedPriceAlertsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FixedPriceAlertsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FixedPriceAlertsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
