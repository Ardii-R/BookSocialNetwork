import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../../services/services';
import { Router } from '@angular/router';
import { BookResponse, PageResponseBookResponse } from '../../../../services/models';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit{

  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 7; // paging size 
  pages: any = [];
  isLastPage: boolean = false;
  borrowMessage = '';
  level: 'error'| 'success' = 'success';


  constructor(private bookService: BookService, private router: Router){}


  ngOnInit(): void {
    console.log("Findind all books from the db...")
    this.findALlBooks();
  }

  findALlBooks() {
    // default size from backend as 10;
    this.bookService.findAllBooks(
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

  // ***** borrow functionality *****
  borrowBook(book: BookResponse) {
    this.borrowMessage = '';
    this.level = "success";

    this.bookService.borrowBook({
      'book-id': book.id as number,
      'connectedUser': "ANY"
    }).subscribe({
      next: () => {
        this.level = "success";
        this.borrowMessage = 'Book successfully added to your list';
      },
      error: (err) => {
        console.log(err);
        this.level = "error";
        this.borrowMessage = err.error.error;
      }
    });
  }
}
