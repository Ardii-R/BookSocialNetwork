import { Component, OnInit } from '@angular/core';
import { BookResponse } from '../../../../services/models/book-response';
import { PageResponseBookResponse } from '../../../../services/models';
import { BookService } from '../../../../services/services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrl: './my-books.component.scss'
})
export class MyBooksComponent implements OnInit {
  
  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 7; // paging size 
  pages: any = [];



  constructor(private bookService: BookService, private router: Router){}


  ngOnInit(): void {
    console.log("Findind all books from the db...")
    this.findALlBooks();
  }

  findALlBooks() {
    // default size from backend as 10;
    this.bookService.findAllBooksByOwner(
      {
        page: this.page,
        size: this.size,
        connectedUser: 'ANY'
      }
    ).subscribe({
      next: (res) => {
        this.bookResponse = res;
        this.pages = new Array(this.bookResponse.totalPages).fill(0).map((_, i) => i);
        console.log(this.bookResponse);
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  // ***** pagination *****

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number -1;
    this.findALlBooks();
  }
  goToNextPage() {
    this.page++;
    this.findALlBooks();
  }
  goToPreviousPage() {
    this.page--;
    this.findALlBooks();
  }
  goToFirstPage() {
    this.page = 0;
    this.findALlBooks();
  }
  goToPage(page: number) {
    this.page = page;
    this.findALlBooks();
  }

  get isLastPage() {
    return this.page === this.bookResponse.totalPages as number - 1;
  }

  // ***** actions ***** 

  editBook(book: BookResponse) {
    this.router.navigate(['books', 'manage', book.id])
  }

  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus(
      { 'book-id': book.id as number,
        connectedUser: "ANY"
      }
    ).subscribe({
      next: () => {
        book.shareable = !book.shareable
      }
    });
  }
  
  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus(
      {
        'book-id': book.id as number,
        connectedUser: "ANY"
      }
    ).subscribe({
      next: () => {
        book.archived =!book.archived
      }
    });
  }

}


