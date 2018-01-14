import { Injectable } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

const toastrOptions = {
  tapToDismiss: true,
  positionClass: 'toast-top-center'
};

@Injectable()
export class NotificationService {

  constructor(private toastrService: ToastrService) { }

  info(message: string) {
    this.toastrService.info(message, '', toastrOptions);
  }

  error(message: string) {
    this.toastrService.error(message, '', toastrOptions);
  }
}
