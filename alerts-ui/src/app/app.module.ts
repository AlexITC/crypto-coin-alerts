import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AlertModule, BsDropdownModule, TooltipModule, ModalModule } from 'ngx-bootstrap';
import { TranslateModule } from '@ngx-translate/core';

import { AppComponent } from './app.component';
import { AppNavbarComponent } from './app-navbar/app-navbar.component';
import { NewAccountComponent } from './new-account/new-account.component';

import { UsersService } from './users.service';

@NgModule({
  declarations: [
    AppComponent,
    AppNavbarComponent,
    NewAccountComponent
  ],
  imports: [
    AlertModule.forRoot(),
    BsDropdownModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    TranslateModule.forRoot()
  ],
  providers: [UsersService],
  bootstrap: [AppComponent]
})
export class AppModule { }
