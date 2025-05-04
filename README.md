# Web Crawler and Search Index Project

## Overview
This project consists of two main components:
1. **Web Crawler**: Crawls Wikipedia pages and saves HTML content
2. **Inverted Index**: Creates a searchable index with TF-IDF ranking and cosine similarity

## Components

### 1. Web Crawler (`Crawler.java`)
A focused crawler that extracts content from Wikipedia pages.

#### Features:
- Depth-first crawling with URL filtering
- Maximum crawl limit (10 pages by default)
- Duplicate URL prevention
- Content length validation (>200 characters)
- HTML document saving

#### Key Methods:
| Method | Description |
|--------|-------------|
| `crawl(String url)` | Recursively crawls Wikipedia pages |
| `saveDocuments()` | Saves crawled pages to specified directory |
| `resetCrawlCount()` | Resets the crawl counter |

#### Usage:
```java
Crawler crawler = new Crawler("path/to/save/directory");
crawler.crawl("https://en.wikipedia.org/wiki/Main_Page");
crawler.saveDocuments();

```
2. Inverted Index (InvertedIndex.java)
Creates a searchable index with TF-IDF ranking and cosine similarity.

Data Structures:
Structure	Purpose
Sources	Maps document IDs to document metadata
Postings	Inverted index (term → list of documents)
Tokens	Term statistics (document frequency)
DocumentVector	TF-IDF vectors for each document
Key Metrics:
Term Frequency (TF): log(1 + frequency_in_doc)

Inverse Document Frequency (IDF): log(N/doc_frequency)

Cosine Similarity: Measures query-document similarity

Search Features:
index.Query("search terms");
