import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewCurrencyAlertsComponent } from './new-currency-alerts.component';

describe('NewCurrencyAlertsComponent', () => {
  let component: NewCurrencyAlertsComponent;
  let fixture: ComponentFixture<NewCurrencyAlertsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewCurrencyAlertsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewCurrencyAlertsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
